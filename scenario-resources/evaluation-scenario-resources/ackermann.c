#include <stdio.h>
#include <stdlib.h>

int ackermann(int lhs, int rhs) {
    if (lhs == 0) {
        return rhs + 1;
    }
    else if (rhs == 0) {
        return ackermann(lhs - 1, 1);
    }
    else {
        return ackermann(lhs - 1, ackermann(lhs, rhs - 1));
    }
}

int main(int argc, char *argv[]) {
    if (argc != 3) {
        printf("This binary requires and accepts exactly 2 arguments\n");
        return 0;
    }

    int lhs = atoi(argv[1]);
    int rhs = atoi(argv[2]);

    int result = ackermann(lhs, rhs);
    printf("ackermann(%d, %d) = %d\n", lhs, rhs, result);

    return 0;
}