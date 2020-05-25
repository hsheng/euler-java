countsByNumber = {}
"""
@param result: { ones, twoCount, totalCount }
@param base: { baseArray, sum, ones, twos }
"""
def resolveOptions(result, base, N: int):
    i = N - base["sum"]
    while i > 0:
        nextBase = { "baseArray": base["baseArray"][:], "sum": base["sum"] + i, "ones": base["ones"], "twos": base["twos"] }
        nextBase["baseArray"].append(i)
        if i == 1:
            nextBase["ones"] += 1
        if i == 2:
            nextBase["twos"] += 1

        if nextBase["sum"] == N:
            result["totalCount"] += 1
            result["ones"] += nextBase["ones"]
            if nextBase["twos"] > 0:
                result["twoCount"] += 1
            print(nextBase["baseArray"])

        else:
            resolveOptions(result, nextBase, N)
        i -= 1

"""
@return { ones, twoCount, totalCount }
"""
def resolveCounts(n: int):
    counts = { "ones": 0, "twoCount": 0, "totalCount": 0 }
    base = { "baseArray": [], "sum": 0, "ones": 0, "twos": 0 }
    resolveOptions(counts, base, n)
    return counts


print("{:>5}: {:>25}, {:>25}, {:>25}".format("n", "ones", "twoCount", "totalCount"))

n = 6
while n == 6:
    counts = resolveCounts(n)
    print("{:5d}: {:25d}, {:25d}, {:25d}".format(n, counts["ones"], counts["twoCount"], counts["totalCount"]))
    n += 1

print("**** DONE ***")
