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
  print(" - 'tape play' to start playing a tape")
  print(" - 'tape pause' to pause playing the tape")
  print(" - 'tape stop' to stop playing and rewind the tape")
  print(" - 'tape rewind' to rewind the tape")
  print(" - 'tape label [name]' to label the tape, leave 'name' empty to get current label")
  print(" - 'tape speed <speed>' to set the playback speed. Needs to be between 0.25 and 2.0")
  print(" - 'tape volume <volume>' to set the volume of the tape. Needs to be between 0.0 and 1.0")
  print(" - 'tape write <path/of/audio/file>' to write to the tape from a file")
  print(" - 'tape write <URL>' to write from a URL")
  print("Other options:")
  print(" '--address=<address>' to use a specific tape drive")
  print(" '--b=<bytes>' to specify the size of the chunks the program will write to a tape")
  print(" '-y' to not ask for confirmation before starting to write")
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
  local s = tonumber(sp)
  if not s or s < 0.25 or s > 2 then
    io.stderr:write("Speed needs to be a number between 0.25 and 2.0")
    return
  end
  tape.setSpeed(s)
  print("Playback speed set to " .. sp)
end

local function volume(vol)
  local v = tonumber(vol)
  if not v or v < 0 or v > 1 then
    io.stderr:write("Volume needs to be a number between 0.0 and 1.0")
    return
  end
  tape.setVolume(v)
  print("Volume set to " .. vol)
end

local function writeTape(path)
  tape.stop()
  tape.seek(-tape.getSize())
  tape.stop() --Just making sure

  local file, msg, _, y
  local block = 2048 --How much to read at a time
  if options.b then
    local nBlock = tonumber(options.b)
    if nBlock then
      print("Setting chunk size to " .. options.b)
      block = nBlock
    else
      io.stderr:write("option --b is not a number.\n")
      return
    end
  end
  if not options.y then
    print("Are you sure you want to write to this tape?")
    print("Type `y` to confirm, `n` to cancel.")
    repeat
      local response = io.read()
      if response and response:lower():sub(1, 1) == "n" then
        print("Canceled.")
        return
      end
    until response and response:lower():sub(1, 1) == "y"
  end
  local bytery = 0 --For the progress indicator
  local filesize = tape.getSize()

  if string.match(path, "https?://.+") then

    if not component.isAvailable("internet") then
      io.stderr:write("This command requires an internet card to run.")
      return false
    end

    local internet = component.internet

    local function setupConnection(url)

      local file, reason = internet.request(url)

      if not file then
        io.stderr:write("error requesting data from URL: " .. reason .. "\n")
        return false
      end

      local connected, reason = false, ""
      for i = 1, 50 do
        connected, reason = file.finishConnect()
        os.sleep(.1)
        if connected or connected == nil then
          break
        end
      end
      
      if connected == nil then
        io.stderr:write("Could not connect to server: " .. reason)
        return false
      end

      local status, message, header = file.response()

      if status then
        status = string.format("%d", status)
        print("Status: " .. status .. " " .. message)
        if status:sub(1,1) == "2" then
          return true, {
            close = function(self, ...) return file.close(...) end,
            read = function(self, ...) return file.read(...) end,
          }, header
        end
        return false
      end
      io.stderr:write("no valid HTTP response - no response")
      return false
    end

    local success, header
    success, file, header = setupConnection(path)
    if not success then
      if file then
        file:close()
      end
      return
    end

    print("Writing...")

    _, y = term.getCursor()

    if header and header["Content-Length"] and header["Content-Length"][1] then
      filesize = tonumber(header["Content-Length"][1])
    end
  else
    local path = shell.resolve(path)
    filesize = fs.size(path)
    print("Path: " .. path)
    file, msg = io.open(path, "rb")
    if not file then
      io.stderr:write("Error: " .. msg)
      return
    end

    print("Writing...")

    _, y = term.getCursor()
  end

  if filesize > tape.getSize() then
    term.setCursor(1, y)
    io.stderr:write("Warning: File is too large for tape, shortening file\n")
    _, y = term.getCursor()
    filesize = tape.getSize()
  end

  repeat
    local bytes = file:read(block)
    if bytes and #bytes > 0 then
      if not tape.isReady() then
        io.stderr:write("\nError: Tape was removed during writing.\n")
        file:close()
        return
      end
      term.setCursor(1, y)
      bytery = bytery + #bytes
      term.write("Read " .. tostring(math.min(bytery, filesize)) .. " of " .. tostring(filesize) .. " bytes...")
      tape.write(bytes)
    end
  until not bytes or bytery > filesize
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
