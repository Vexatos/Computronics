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

local function pretty(text)
  if not text:find(".", 1, true) then
    text = text .. "."
  end
  local leftSide = text:sub(1, text:find(".", 1, true) - 1, nil)
  local rightSide = text:sub(text:find(".", 1, true) + 1, nil)
  while #rightSide < 2 do
    rightSide = rightSide .. "0"
  end
  return leftSide .. "." .. rightSide, leftSide
end

local function explode(fuse)
  local sd = component.self_destruct

  if fuse <= 0 then
    io.stderr:write("Invalid number. Needs to be greater than 0.\n")
    return
  elseif fuse > 100000 then
    io.stderr:write("Invalid number. Needs to be smaller than 100000.\n")
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

  if not term.isAvailable() then
    io.stderr:write("no primary screen found\n")
    return
  end

  if options.t then
    local _, y = term.getCursor()
    local resX = gpu.getResolution()
    repeat
      local timeLeft = sd.time()
      if timeLeft < 0 then
        timeLeft = 0
      end
      local timeText = "Time left: " .. pretty(tostring(timeLeft))
      gpu.fill(#timeText + 1, y, resX - #timeText, 1, " ")
      term.setCursor(1, y)
      term.write(timeText)
      os.sleep(0)
    until timeLeft <= 0
  else
    term.clear()
    local prevX, prevY = gpu.getResolution()
    local maxX, maxY = gpu.maxResolution()
    local leftText = "Time left:"
    local resX = math.min(#leftText, maxX)
    local resY = math.min(5, maxY)
    gpu.setResolution(resX, resY)
    term.setCursor(math.max(1, math.ceil((resX / 2)) - math.ceil((#leftText / 2))), 2)
    term.write(leftText)

    local dotPlace = math.ceil((resX / 2))
    repeat
      local timeLeft = sd.time()
      if timeLeft < 0 then
        timeLeft = 0
      end
      local timeText, leftSide = pretty(tostring(timeLeft))

      local xPos = dotPlace - #leftSide
      gpu.fill(1, 3, xPos - 1, 1, " ")
      gpu.fill(xPos + #timeText, 3, resX - xPos - #timeText, 1, " ")
      term.setCursor(xPos, 3)
      term.write(timeText)
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
