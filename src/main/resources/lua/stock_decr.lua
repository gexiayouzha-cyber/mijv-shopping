local stockKey = KEYS[1]
local decrement = tonumber(ARGV[1])
local stock = tonumber(redis.call('GET', stockKey) or 0)

--这个不怕传小数吗
if decrement == nil or decrement<=0 then
    return -2
end
if stock < decrement then
    return -1
end
redis.call('DECRBY', stockKey, decrement)
return stock-decrement
