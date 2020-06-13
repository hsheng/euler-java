import timeit
import time
from functools import reduce
from math import sqrt

def factors(n):
    step = 2 if n%2 else 1
    return sorted(set(reduce(list.__add__,
                ([i, n//i] for i in range(1, int(sqrt(n))+1, step) if n % i == 0))))
    
if __name__ == "__main__":
    for input in [2188867, 438251, 11033873, 110777667]:
        f = factors(input)
        print("{}, {}".format(input, f))