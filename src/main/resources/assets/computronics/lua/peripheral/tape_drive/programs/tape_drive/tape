--[[ tape program, provides basic tape modification and access tools
Authors: gamax92, Bizzycola, Vexatos
]]
local args = { ... }
local tape = peripheral.find("tape_drive")
if not tape then
  print("This program requires a tape drive to run.")
  return
end

local function printUsage()
  print("Usage:")
  print(" - 'tape play' to start playing a tape")
  print(" - 'tape pause' to pause playing the tape")
  print(" - 'tape stop' to stop playing and rewind the tape")
  print(" - 'tape rewind' to rewind the tape")
  print(" - 'tape wipe' to wipe any data on the tape and erase it completely")
  print(" - 'tape label [name]' to label the tape, leave 'name' empty to get current label")
  print(" - 'tape speed <speed>' to set the playback speed. Needs to be between 0.25 and 2.0")
  print(" - 'tape volume <volume>' to set the volume of the tape. Needs to be between 0.0 and 1.0")
  print(" - 'tape write <path/of/audio/file>' to write to the tape from a file")
  return
end

if not tape.isReady() then
  printError("The tape drive does not contain a tape.")
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
    printError("Speed needs to be a number between 0.25 and 2.0")
    return
  end
  tape.setSpeed(s)
  print("Playback speed set to " .. sp)
end

local function volume(vol)
  local v = tonumber(vol)
  if not v or v < 0 or v > 1 then
    printError("Volume needs to be a number between 0.0 and 1.0")
    return
  end
  tape.setVolume(v)
  print("Volume set to " .. vol)
end

local function confirm(msg)
  print(msg)
  print("Type `y` to confirm, `n` to cancel.")
  repeat
    local response = read()
    if response and response:lower():sub(1, 1) == "n" then
      print("Canceled.")
      return false
    end
  until response and response:lower():sub(1, 1) == "y"
  return true
end

local function wipe()
  if not confirm("Are you sure you want to wipe this tape?") then return end
  local k = tape.getSize()
  tape.stop()
  tape.seek(-k)
  tape.stop() --Just making sure
  tape.seek(-90000)
  local s = string.rep("\xAA", 8192)
  for i = 1, k + 8191, 8192 do
    tape.write(s)
  end
  tape.seek(-k)
  tape.seek(-90000)
  print("Done.")
end

local function writeTape(relPath)
  local file, msg, _, y, success
  local block = 8192 --How much to read at a time

  if not confirm("Are you sure you want to write to this tape?") then return end
  tape.stop()
  tape.seek(-tape.getSize())
  tape.stop() --Just making sure

  local path = shell.resolve(relPath)
  local bytery = 0 --For the progress indicator
  local filesize = fs.getSize(path)
  print("Path: " .. path)
  file, msg = fs.open(path, "rb")
  if not fs.exists(path) then msg = "file not found" end
  if not file then
    printError("Failed to open file " .. relPath .. (msg and ": " .. tostring(msg)) or "")
    return
  end

  print("Writing...")

  _, y = term.getCursorPos()

  if filesize > tape.getSize() then
    term.setCursorPos(1, y)
    printError("Error: File is too large for tape, shortening file")
    _, y = term.getCursorPos()
    filesize = tape.getSize()
  end

  repeat
    local bytes = {}
    for i = 1, block do
      local byte = file.read()
      if not byte then break end
      bytes[#bytes + 1] = byte
    end
    if #bytes > 0 then
      if not tape.isReady() then
        io.stderr:write("\nError: Tape was removed during writing.\n")
        file.close()
        return
      end
      term.setCursorPos(1, y)
      bytery = bytery + #bytes
      term.write("Read " .. tostring(math.min(bytery, filesize)) .. " of " .. tostring(filesize) .. " bytes...")
      for i = 1, #bytes do
        tape.write(bytes[i])
      end
      sleep(0)
    end
  until not bytes or #bytes <= 0 or bytery > filesize
  file.close()
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
elseif args[1] == "wipe" then
  wipe()
else
  printUsage()
end
