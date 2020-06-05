def isDuoDigitNum(n:int):
    chrset = set(list(str(n)))
    return len(chrset) <= 2
    
def findDuoNum(nonDuoNum: int, max: int):
    multiple = 2
    duoNum = nonDuoNum << 1
    while not isDuoDigitNum(duoNum):
        duoNum += nonDuoNum
        multiple += 1
        
    num = nonDuoNum
    m = multiple >> 1
    while num <= max and m > 0 and (m & 0x1) == 0:
        num = num << 1
        print("     ", nonDuoNum, num)
        m = m >> 1
        
    return (duoNum, multiple)


if __name__ == "__main__":

    max = 50000
    nonDuoNum = 102
    num = nonDuoNum
    m = 1
    while num < max:
        (d2, m2) = findDuoNum(num, max)
        print(num, m, d2, m2)
        num += nonDuoNum
        m += 1