--[[ basic self-destruction program
Author: Vexatos
]]
local component = require("component")
local shell = require("shell")
local term = require("term")

local args, options = shell.parse(...)

if not component.isAvailable("self_destruct") then
  io.stderr:write("This program requires a self-destructing card to run.")
  return
end

if not component.isAvailable("gpu") then
  io.stderr:write("This program requires a graphics card to run.")
  return
end
local gpu = component.gpu

local function printUsage()
  print("Usage:")
  print("'explode [-sty] <time>' to start the fuse for the specified amount of seconds")
  print(" -s: Start the fuse silently (without displaying a countdown)")
  print(" -t: Displays the countdown in the shell (not taking up the entire screen)")
  print(" -y: Do not display confirmation message")
  print("-s and -t cannot be combined!")
  return
end

local function explode(fuse)
  local sd = component.self_destruct

  if fuse <= 0 then
    io.stderr:write("Invalid number. Needs to be greater than 0.\n")
    return
  end

  if not options.y then
    print("Do you really want to start the fuse? type 'y' to confirm, anything else to stop.")

    local function getResponse(resp)
      if not resp or #resp == 0 then
        io.stderr:write("Invalid input. Please type again.\n")
        return false
      end
      return true
    end

    local response
    repeat
      response = io.read()
    until getResponse(response)
    if response:lower():sub(1, 1) ~= "y" then
      print("Explosion aborted.")
      return
    end
  end

  if options.s then
    sd.start(fuse)
    return
  end
  print("Starting fuse")

  sd.start(fuse)

  if options.t then
    local _, y = term.getCursor()
    repeat
      local timeLeft = sd.time()
      term.setCursor(1, y)
      term.write("Time left: " .. tostring(timeLeft))
      os.sleep(0.1)
    until timeLeft <= 0
    term.setCursor(1, y)
    term.write("Time left: 0.00\n")
  else
    term.clear()
    local prevX, prevY = gpu.getResolution()
    local maxX, maxY = gpu.maxResolution()
    local leftText = "Time left:"
    local resX = math.min(#leftText, maxX)
    local resY = math.min(5, maxY)
    gpu.setResolution(resX, resY)

    local dotPlace = math.ceil((resX / 2))
    repeat
      local timeLeft = sd.time()
      local timeText = tostring(timeLeft)
      term.setCursor(math.max(1, math.ceil((resX / 2)) - math.ceil((#leftText / 2))), 2)
      term.write("Time left:")
      local xPos
      if timeText:find(".", 1, true) then
        xPos = dotPlace - #(timeText:sub(1, timeText:find(".", 1, true) - 1, nil))
      else
        xPos = dotPlace - #timeText
      end
      term.setCursor(xPos, 3)
      term.write(tostring(timeLeft))
      os.sleep(0)
    until timeLeft <= 0
    gpu.setResolution(prevX, prevY)
    term.clear()
  end
end

if args[1] and tonumber(args[1]) then
  if options.s and options.t then
    io.stderr:write("Cannot combine options -s and -t\n")
    printUsage()
    return
  end
  explode(tonumber(args[1]))
else
  printUsage()
end
