import numpy as np

#L1  = [0, 1, 1, 0, 0, 0, 0, 0, 0, 0]
L1  = [0, 1, 1, 0, 1, 0, 0, 0, 0, 0]
L2  = [1, 0, 0, 1, 0, 0, 0, 0, 0, 0]
#L3  = [0, 1, 0, 0, 0, 0, 0, 0, 0, 0]
L3  = [0, 1, 0, 0, 0, 0, 1, 0, 0, 0]
L4  = [0, 1, 1, 0, 0, 0, 0, 0, 0, 0]
L5  = [0, 0, 0, 0, 0, 1, 1, 0, 0, 0]
L6  = [0, 0, 0, 0, 0, 0, 1, 1, 0, 0]
L7  = [0, 0, 0, 0, 1, 1, 1, 1, 1, 1]
L8  = [0, 0, 0, 0, 0, 0, 1, 0, 1, 0]
L9  = [0, 0, 0, 0, 0, 0, 1, 0, 0, 1]
L10 = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0]

L = np.array([L1, L2, L3, L4, L5, L6, L7, L8, L9, L10])

ITERATIONS = 100

### TODO 1: Compute stochastic matrix M
def getM(L):
    M = np.zeros([10, 10], dtype=float)
    # number of outgoing links
    c = np.zeros([10], dtype=int)
    
    # SOLVE 1 ************************************************
    for i in range(10):
        c[i] = sum(L[i])
    print("DEBUG")
    print(c)   
    print("DEBUG")    
    for i in range(10):
        for j in range(10):
            if L[j][i] != 0:
                M[i][j] = 1 / c[j]
            else:
                M[i][j] = 0
    # ******************************************************
    
    return M #L
    
print("Matrix L (indices)")
print(L)    

M = getM(L)

print("Matrix M (stochastic matrix)")
print(M)
print("\n")

### TODO 2: compute pagerank with damping factor q = 0.15
### Then, sort and print: (page index (first index = 1 add +1) : pagerank)
### (use regular array + sort method + lambda function)
print("PAGERANK")

q = 0.15

pr = np.zeros([10], dtype=float)

# SOLVE 2 ************************************************
for i in range(ITERATIONS):
    for j in range(10):
        pr[j]=q + ((1 - q) * sum([pr[k] * M[j][k] for k in range(10)]))
pr = np.sort(pr)[::-1]
for i in range(10):
	print(str(i) + ": " + str(pr[i]/sum(pr)))
print("\n")
# ******************************************************
    
### TODO 3: compute trustrank with damping factor q = 0.15
### Documents that are good = 1, 2 (indexes = 0, 1)
### Then, sort and print: (page index (first index = 1, add +1) : trustrank)
### (use regular array + sort method + lambda function)
print("TRUSTRANK (DOCUMENTS 1 AND 2 ARE GOOD)")

q = 0.15

d = np.zeros([10], dtype=float)
# SOLVE 3 ************************************************
d[0] = 1
d[1] = 1
# ******************************************************

tr = [v for v in d]

# SOLVE 3 ************************************************
for i in range(ITERATIONS):
    for j in range(10):
        tr[j]= (d[j] * q) + ((1 - q) * sum([tr[k] * M[j][k] for k in range(10)]))
tr = np.sort(tr)[::-1]
for i in range(10):
	print(str(i) + ": " + str(tr[i]/sum(tr)))
# ******************************************************
    
### TODO 4: Repeat TODO 3 but remove the connections 3->7 and 1->5 (indexes: 2->6, 0->4) 
### before computing trustrank
