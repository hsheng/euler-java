"""
Solution for problem 710 on eulerproject.net
"""
from dataclasses import dataclass
import math
from builtins import min
import json
import pathlib
import datetime
import time
import sys
from dataclasses_json import dataclass_json
from itertools import count


# [ totalCount, hasTwoCount ]    
countsByNum = \
    [ [  32,  20],      # 6
      [  64,  43],      # 7
      [ 128,  91],      # 8
      [ 256, 191],      # 9
      [ 512, 398] ]     # 10

bn = 10
n = 20
twopalCount = 824
st = time.time()

def checkResult(n, twopalCount):
    global st

    ending = str(twopalCount)[-6:]
    if (n & 0xFFF) == 0:
        print(n, ending, (time.time() - st))
        
    if ending == "000000":
        print("=== DONE ===")
        print("n={}".format(n))
        
        et = time.time()
        print("=== time: {} secs ===".format((et - st)))
        sys.exit()
        
# @param n - ODD number       
def getTwopalCount(n: int) -> int:
    global twopalCount
    global countsByNum

    bn : int = n >> 1;
    
    twopalCount += countsByNum[3][1] - countsByNum[3][0]
    checkResult(n, twopalCount)

    bn += 1
    c0 = countsByNum[4][0] << 1
    c1 = 3 * countsByNum[4][1] - countsByNum[3][1] - (countsByNum[2][1] << 1) + countsByNum[1][1] - (countsByNum[0][1] << 1)

    countsByNum[0] = countsByNum[1]
    countsByNum[1] = countsByNum[2]
    countsByNum[2] = countsByNum[3]      
    countsByNum[3] = countsByNum[4]
    countsByNum[4] = [c0, c1]
    
    twopalCount += countsByNum[4][1] + countsByNum[3][0] - countsByNum[3][1]
    checkResult((n+1), twopalCount)


if __name__ == "__main__":
    
    n = 21
    while True:
        getTwopalCount(n)
        n += 2
    
    et = time.time()
    print("=== time: {} secs ===".format((et - st)))
    