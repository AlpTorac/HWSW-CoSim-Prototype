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
    if (argc != 2) {
        printf("This binary requires and accepts exactly 1 argument\n");
        return 0;
    }

    int rhs = atoi(argv[1]);

    int result = ackermann(2, rhs);
    printf("ackermann(%d, %d) = %d\n", 2, rhs, result);

    return 0;
}