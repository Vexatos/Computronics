local path = shell.path()
path = path .. ":/rom/programs/tape_drive"
shell.setPath(path)

--from startup.lua
local function completeMultipleChoice(text, options, addSpaces)
  local results = {}
  for n = 1, #options do
    local option = options[n]
    if option and #option + (addSpaces and 1 or 0) > #text and string.sub(option, 1, #text) == text then
      local result = string.sub(option, #text + 1)
      if addSpaces then
        table.insert(results, result .. " ")
      else
        table.insert(results, result)
      end
    end
  end
  return results
end

local tapeOptions = { "play", "stop", "pause", "rewind", "label ", "speed ", "volume ", "write " }

local function completeTape(shell, index, text, previousText)
  if index == 1 then
    return completeMultipleChoice(text, tapeOptions)
  elseif index == 2 and previousText[2] == "write" then
    return fs.complete(text, shell.dir(), true, false)
  end
end

shell.setCompletionFunction("rom/programs/tape_drive/tape", completeTape)
