import numpy as np
def main(array):
    threshold = 13.1
    count = 0
    below_threshold = True
    array = array[1:-1]
    items = array.split(',')

    for i in range(len(items)):
        if float(items[i]) > threshold:
            items[i] = True
        else:
            items[i] = False
    flag_up = False
    flag_down = False
    curr = False
    for i in range(1,len(items)):
        if items[i] and not curr:
            flag_up = False
        elif not items[i] and curr:
            flag_down = True
        curr = items[i]
        if flag_down and flag_down:
            count += 1
            flag_down = False
            flag_up = False

    return count





    for num in items:
        print(num)
        if below_threshold and float(num) > tresh:
            count += 1
        below_threshold = float(num) <= tresh

    return count


