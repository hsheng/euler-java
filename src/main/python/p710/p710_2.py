filename = "countsByNumber.txt"
countsByNumber = []

"""
@param result [<total-count>, <twopal-count>]
@param a { isTwopal: boolean, base: []}
"""
def findCountsRecursively(result:[], a:{}, asum: int, t:int):
    i = t - asum
    while i > 0:
        na = a["base"][:]
        na.append(i)
        nasum = asum + i
        isTwopal = a["isTwopal"] or i == 2

        if nasum == t:
            result[0] += 1
            if isTwopal:
                result[1] += 1
        else:
            findCountsRecursively(result, {"isTwopal": isTwopal, "base": na}, nasum, t)
        i -= 1

"""
@return [ total-count, twopal-count]
"""
def findCounts(n: int):
    counts = [0, 0]
    findCountsRecursively(counts, {"isTwopal": False, "base":[]}, 0, n)
    return counts

def saveCounts(n, counts):
    with open(filename, mode="a") as f:
        print("{} {} {}".format(n, counts[0], counts[1]), file=f)

def getCountsByNumber(n: int):
    global countsByNumber

    if n > len(countsByNumber) - 1:
        countsByNumber = countsByNumber + [None] * (n - len(countsByNumber) + 1)
    
    counts = countsByNumber[n]
    if counts is None:
        counts = findCounts(n)
        countsByNumber[n] = counts
        saveCounts(n, counts)

    return counts

def findTwopals(n: int):
    total = 0
    if (n & 0x1) == 0:
        i = 0; step = 2; stop = n - 4
        while i <= stop:
            counts = getCountsByNumber((n - i) >> 1)
            if i == 2:
                total += counts[0]
            else:
                total += counts[1]
            i += step
    else:
        i = 1; step = 2; stop = n - 4
        while i <= stop:
            counts = getCountsByNumber((n - i) >> 1)
            total += counts[1]
            i += step

    return total

def init():
    global countsByNumber
    with open(filename, mode="r") as f:
        lines = f.readlines()
        for line in lines:
            a = line.split()
            idx = int(a[0])
            totalCount = int(a[1])
            twopalCount = int(a[2])

            if idx > len(countsByNumber) - 1:
                countsByNumber = countsByNumber + [None] * (idx - len(countsByNumber) + 1)
            countsByNumber[idx] = [totalCount, twopalCount]

init()

n = 6
while True:
    count = findTwopals(n)
    print("t({}) = {}".format(n, count))
    if count % 1000000 == 0:
        break
    else:
        n += 1

print("**** DONE ***")
