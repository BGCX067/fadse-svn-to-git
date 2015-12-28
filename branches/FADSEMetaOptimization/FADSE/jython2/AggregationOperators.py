'''
Created on Jul 21, 2011

@author: camil
'''
def Sum(metrics):
    sum = 0;
    for indicator in metrics:
        sum += indicator.getValue()
    return sum