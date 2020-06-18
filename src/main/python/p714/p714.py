from time import time
from datetime import datetime
import os
import fnmatch
import json
import math

def isDuoDigitNum(n:int):
    chrset = set(list(str(n)))
    return len(chrset) <= 2

def bruFind(num: int):
    if isDuoDigitNum(num):
        return {"num":num, "solved":True, "duo":num, "multiple":1, "time":0}

    t1 = time()

    duo = num << 1
    step = 2
    while not isDuoDigitNum(duo):
        duo += num
        step += 1

    t2 = time()
    return {"num":num, "solved":True, "duo":duo, "multiple":step, "time":(t2 - t1)}

def bruFindTimeout(num: int, timeout:int):
    if isDuoDigitNum(num):
        return {"num":num, "solved":True, "duo":num, "multiple":1, "time":0}

    t1 = time()

    duo = num << 1
    step = 2
    while not isDuoDigitNum(duo):
        if (step & 0x3FF) == 0:
            t2 = time()
            if (t2 - t1) > timeout:
                return {"num":num, "solved":False, "duo":duo, "multiple":step, "time":(t2 - t1)}
        duo += num
        step += 1

    t2 = time()
    return {"num":num, "solved":True, "duo":duo, "multiple":step, "time":(t2 - t1)}



def bfsFindByDigits(num:int, d1:int, d2:int, maxDuoLen:int):
    t1 = time()

    modMap = {}
    masterQueue = [str(d1)]
    d1s = str(min(d1, d2))
    d2s = str(max(d1, d2))

    nLenQueueD1s = []
    nLenQueueD2s = []
    nLen = 0

    while len(masterQueue) > 0:
        ns = masterQueue.pop(0)
        if len(ns) > maxDuoLen:
            break

        if nLen < len(ns):
            masterQueue.extend(nLenQueueD1s)
            masterQueue.extend(nLenQueueD2s)
            nLenQueueD1s = []
            nLenQueueD2s = []
            nLen = len(ns)
            modMap = {}

        nn = int(ns)
        if nn <= num:
            nLenQueueD1s.append(d1s + ns)
            nLenQueueD2s.append(d2s + ns)
            mod = nn % num
            modMap[str(mod)] = nn
        else:
            mod = nn % num
            if not mod:
                return {"num":num, "solved":True, "duo":nn, "multiple":int(nn/num), "time":(time() - t1)}
            else:
                modStr = str(mod)
                if not modStr in modMap:
                    modMap[modStr] = nn
                    nLenQueueD1s.append(d1s + ns)
                    nLenQueueD2s.append(d2s + ns)

        if not masterQueue:
            masterQueue.extend(nLenQueueD1s)
            masterQueue.extend(nLenQueueD2s)
            nLenQueueD1s = []
            nLenQueueD2s = []

    return {"num":num, "solved":False, "duo":num, "multiple":1, "time":(time() - t1)}

def getD1OptionsByMod(mod: int):
    return {
        "0": [0],
        "1": [1, 2, 3, 4, 5, 6, 7, 8, 9],
        "3": [1, 2, 3, 4, 5, 6, 7, 8, 9],
        "7": [1, 2, 3, 4, 5, 6, 7, 8, 9],
        "9": [1, 2, 3, 4, 5, 6, 7, 8, 9],
        "2": [2, 4, 6, 8, 0],
        "4": [2, 4, 6, 8, 0],
        "6": [2, 4, 6, 8, 0],
        "8": [2, 4, 6, 8, 0],
        "5": [5, 0]
    }[str(mod)]

def getD2OptionsByD1(d1:int):
    if d1:
        return [1,2,3,4,5,6,7,8,9,0]
    else:
        return [1,2,3,4,5,6,7,8,9]

def bfsFind(num:int, maxDuoLen:int):
    if isDuoDigitNum(num):
        return {"num":num, "solved":True, "duo":num, "multiple":1, "time":0}

    t1 = time()

    candidate = {"num":num, "solved":False, "duo":num, "multiple":1, "time":0}
    mod = num % 10
    for d1 in getD1OptionsByMod(mod):
        for d2 in getD2OptionsByD1(d1):
            result = bfsFindByDigits(num, d1, d2, maxDuoLen)
            if result["solved"]:
                if (not candidate["solved"]) or (result["duo"] <= candidate["duo"]):
                    candidate = result
    
    t2 = time()
    candidate["time"] = (t2 - t1)
    return candidate

def mixFind(num:int, timeout:int, maxDuoLen:int):
    t1 = time()
    r = bruFindTimeout(num, timeout)
    if not r["solved"]:
        r = bfsFind(num, maxDuoLen)
    r["time"] = (time() - t1)
    return r    

#
# find duo-digit-multiple for each num in a range
#
def bfsFindRange(start:int, end:int, maxDuoLen:int):
    map = {}
    for num in range(start, end + 1):
        r = bfsFind(num, maxDuoLen)
        map[str(r["num"])] = r
    return map

def bruFindRange(start:int, end:int):
    map = {}
    for num in range(start, end + 1):
        r = bruFind(num)
        map[str(r["num"])] = r
    return map

def mixFindRange(start:int, end:int, timeout:int, maxDuoLen:int):
    map = {}
    for num in range(start, end + 1):
        r = mixFind(num, timeout, maxDuoLen)
        map[str(r["num"])] = r
    return map

# sum-up duo-digit-multiple
def sumup(duoList):
    t1 = time()
    summary = 0
    solved = True
    for duo in duoList:
        solved = (solved and duo["solved"])
        summary += duo["duo"]
    return {"summary": summary, "solved":solved, "time":(time() - t1)}

#
# summary methods
#
def bfsRangeSum(start:int, end:int, maxDuoLen:int):
    t1 = time()
    r = bfsFindRange(start, end, maxDuoLen)
    s = sumup(r.values())
    return {"m":"bfs", "start":start, "end":end, "summary":s["summary"], "solved":s["solved"], "time":(time() - t1)}

def bruRangeSum(start:int, end:int):
    t1 = time()
    r = bruFindRange(start, end)
    s = sumup(r.values())
    return {"m":"bru", "start":start, "end":end, "summary":s["summary"], "solved":s["solved"], "time":(time() - t1)}

def mixRangeSum(start:int, end:int, timeout:int, maxDuoLen:int):
    t1 = time()
    r = mixFindRange(start, end, timeout, maxDuoLen)
    s = sumup(r.values())
    return {"m":"mix", "start":start, "end":end, "summary":s["summary"], "solved":s["solved"], "time":(time() - t1)}

def compareRangeFind(start:int, end:int, timeout:int, maxDuoLen:int):
    print("compare bru/mix find result: [{},{}] {}".format(start, end, datetime.now().strftime("%H:%M:%S")))
    mixDict = mixFindRange(start, end, timeout, maxDuoLen)
    print("done mixFindRange {}".format(datetime.now().strftime("%H:%M:%S")))
    bruDict = bruFindRange(start, end)
    print("done bruFindRange {}".format(datetime.now().strftime("%H:%M:%S")))

    for key in range(start, end+1):
        keyStr = str(key)
        bruNum = bruDict[keyStr]
        mixNum = mixDict[keyStr]

        if bruNum["duo"] != mixNum["duo"]:
            print("{:5d}: {:5d}, {:6.2f}    {:5d}, {:6.2f}".format(bruNum["num"], bruNum["duo"], bruNum["time"], mixNum["duo"], mixNum["time"]))
    print("*** Done compareRangeFind ***")

def compareRangeSum(start:int, end:int, timeout:int, maxDuoLen:int):
    print("compare bru/mix sum result: [{},{}] {}".format(start, end, datetime.now().strftime("%H:%M:%S")))
    mixSum = mixRangeSum(start, end, timeout, maxDuoLen)
    print("done mixRangeSum {}".format(datetime.now().strftime("%H:%M:%S")))
    bruSum = bruRangeSum(start, end)
    print("done bruRangeSum {}".format(datetime.now().strftime("%H:%M:%S")))
    if bruSum["summary"] != mixSum["summary"]:
        print("***bru", bruSum)
        print("***mix", mixSum)
    else:
        print("bru", bruSum)
        print("mix", mixSum)

def resolve(boundary):
    t1 = time()
    timeout = 2
    maxDuoLen = 30
    solved = True
    result = 0
    chunkSize = 1000
    chunks = int(math.ceil(boundary/chunkSize))
    for chunk in range(1, chunks + 1):
        start = (chunk - 1) * chunkSize + 1
        end = chunk * chunkSize
        r = mixRangeSum(start, end, timeout, maxDuoLen)
        print("mix [{:5d}, {:5d}] {} {:.12e} [{: >25}] time: {:7.2f}s".format(start, end, r["solved"], r["summary"], str(r["summary"]), r["time"]))

        solved = solved and r["solved"]
        result += r["summary"]
    print("*Done* [{:5d}, {:5d}] solved={} {:.12e} [{: >30}] time={:.2f}s".format(1, boundary, solved, result, result, (time() - t1)))

if __name__ == "__main__":

    resolve(50000)
