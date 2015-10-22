xxx=project name say

0. Gen a key pair: Console > EC2 > Key Pairs > create pair, call it xxx-key-pair, move into ~/.ssh
1. Console > EMR
2. Click advanced options
3. cluster name: xxx-spark-cluster
4. suggest use memory optimised types: r3.xlarge
5. Add zeppelin sandbox and spark to the application list
6. use key pair xxx-key-pair
7. click create
8. wait (best to wait until it not provisioning cos it won't have everything installed yet)
9. when the SSH hyperlink shows up it will give you the command to be able ssh in
