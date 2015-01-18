--[[ tape program, provides basic tape modification and access tools
Authors: Bizzycola and Vexatos
]]
local component = require("component")
local fs = require("filesystem")
local shell = require("shell")
local term = require("term")

local args, options = shell.parse(...)

if not component.isAvailable("tape_drive") then
  io.stderr:write("This program requires a tape drive to run.")
  return
end

local function printUsage()
  print("Usage:")
  print("'tape play' to start playing a tape")
  print("'tape pause' to pause playing the tape")
  print("'tape stop' to stop playing and rewind the tape")
  print("'tape rewind' to rewind the tape")
  print("'tape label [name]' to label the tape, leave 'name' empty to get current label")
  print("'tape speed <speed>' to set the playback speed. Needs to be between 0.25 and 2.0")
  print("'tape volume <volume>' to set the volume of the tape. Needs to be between 0.0 and 1.0")
  print("'tape write <path/of/audio/file>' to write to the tape from a file")
  print("'tape write <URL>' to write from a URL")
  print("Other options:")
  print("'--address=<address>' to use a specific tape drive")
  return
end

local function getTapeDrive()
  --Credits to gamax92 for this
  local tape
  if options.address then
    if type(options.address) ~= "string" then
      io.stderr:write("'address' may only be a string.")
      return
    end
    local fulladdr = component.get(options.address)
    if fulladdr == nil then
      io.stderr:write("No component at this address.")
      return
    end
    if component.type(fulladdr) ~= "tape_drive" then
      io.stderr:write("No tape drive at this address.")
      return
    end
    tape = component.proxy(fulladdr)
  else
    tape = component.tape_drive
  end
  return tape
  --End of gamax92's part
end

local tape = getTapeDrive()

if not tape.isReady() then
  io.stderr:write("The tape drive does not contain a tape.")
  return
end

local function label(name)
  if not name then
    if tape.getLabel() == "" then
      print("Tape is currently not labeled.")
      return
    end
    print("Tape is currently labeled: " .. tape.getLabel())
    return
  end
  tape.setLabel(name)
  print("Tape label set to " .. name)
end

local function rewind()
  print("Rewound tape")
  tape.seek(-tape.getSize())
end

local function play()
  if tape.getState() == "PLAYING" then
    print("Tape is already playing")
  else
    tape.play()
    print("Tape started")
  end
end

local function stop()
  if tape.getState() == "STOPPED" then
    print("Tape is already stopped")
  else
    tape.stop()
    tape.seek(-tape.getSize())
    print("Tape stopped")
  end
end

local function pause()
  if tape.getState() == "STOPPED" then
    print("Tape is already paused")
  else
    tape.stop()
    print("Tape paused")
  end
end

local function speed(sp)
  if not tonumber(sp) then
    io.stderr:write("Speed needs to be a number between 0.25 and 2.0")
    return
  end
  tape.setSpeed(tonumber(sp))
  print("Playback speed set to " .. sp)
end

local function volume(vol)
  if not tonumber(vol) then
    io.stderr:write("Volume needs to be a number between 0.0 and 1.0")
    return
  end
  tape.setVolume(tonumber(vol))
  print("Volume set to " .. vol)
end

local function writeTape(path)
  tape.stop()
  tape.seek(-tape.getSize())
  tape.stop() --Just making sure

  local file, msg, _, y, success
  local block = 1024 --How much to read at a time
  local bytery = 0 --For the progress indicator
  local filesize = tape.getSize()

  if string.match(path, "https?://.+") then

    if not component.isAvailable("internet") then
      io.stderr:write("This program requires an internet card to run.")
      return false
    end

    local internet = require("internet")
    local header = ""

    local function setupConnection(addr)
      if string.match(addr, "https://") then
        io.stderr:write("unsupported URL (HTTPS is not supported, use HTTP)")
        return false
      end
      local url = string.gsub(addr, "http://", "", 1)
      local domain = string.gsub(url, "/.*", "", 1)
      local path = string.gsub(url, ".-/", "/", 1)
      local header = ""

      file = internet.open(domain, 80)
      file:setTimeout(10)

      file:write("GET " .. path .. " HTTP/1.1\r\nHost: " .. domain .. "\r\nConnection: close\r\n\r\n")

      repeat
        local hBlock = file:read(block)
        if not hBlock or #hBlock <= 0 then
          io.stderr:write("no valid HTTP response - malformed header")
          return false
        end
        header = header .. hBlock
      until string.find(header, "\r\n\r\n")

      if string.match(header, "HTTP/1.1 ([^\r\n]-)\r\n") then
        local status = string.match(header, "HTTP/1.1 ([^\r\n]-)\r?\n")
        local location = string.match(header, "[Ll]ocation: (.-)\r\n")
        if string.match(status, "3%d%d") then
          if location ~= addr then
            file:close()
            print("Redirecting to " .. location)
            return setupConnection(location)
          end
        end
        if string.match(status, "2%d%d") then
          print("Status: " .. status)
          print("Domain: " .. domain)
          print("Path: " .. path)
          return true, file, header
        end
        io.stderr:write(status)
        return false
      end
      io.stderr:write("no valid HTTP response - no response")
      return false
    end

    success, file, header = setupConnection(path)
    if not success then
      if file then
        file:close()
      end
      return
    end

    print("Writing...")

    _, y = term.getCursor()

    if string.match(header, "Content%-Length: (%d-)\r\n") then
      filesize = tonumber(string.match(header, "Content%-Length: (%d-)\r\n"))
    end
    local bytes = string.gsub(header, ".-\r\n\r\n", "", 1)
    if bytes and #bytes > 0 then
      term.setCursor(1, y)
      bytery = bytery + #bytes
      term.write("Read " .. tostring(bytery) .. " bytes...")
      tape.write(bytes)
    end
  else
    local path = shell.resolve(path)
    filesize = fs.size(path)
    print("Path: " .. path)
    file, msg = io.open(shell.resolve(path), "rb")
    if not file then
      io.stderr:write("Error: " .. msg)
      return
    end

    print("Writing...")

    _, y = term.getCursor()
  end

  if filesize > tape.getSize() then
    term.setCursor(1, y)
    io.stderr:write("Error: File is too large for tape, shortening file")
    y = y + 1
    filesize = tape.getSize()
  end
  
  repeat
    local bytes = file:read(block)
    if bytes and #bytes > 0 then
      term.setCursor(1, y)
      bytery = bytery + #bytes
      term.write("Read " .. tostring(bytery) .. " of " .. tostring(filesize) .. " bytes...")
      tape.write(bytes)
    end
  until not bytes
  file:close()
  tape.stop()
  tape.seek(-tape.getSize())
  tape.stop() --Just making sure
  print("\nDone.")
end

if args[1] == "play" then
  play()
elseif args[1] == "stop" then
  stop()
elseif args[1] == "pause" then
  pause()
elseif args[1] == "rewind" then
  rewind()
elseif args[1] == "label" then
  label(args[2])
elseif args[1] == "speed" then
  speed(args[2])
elseif args[1] == "volume" then
  volume(args[2])
elseif args[1] == "write" then
  writeTape(args[2])
else
  printUsage()
end
