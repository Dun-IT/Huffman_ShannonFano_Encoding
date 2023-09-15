import java.util.Scanner;
import java.lang.Math;

public class Calculate {
    public static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        int M, N;

        do {
            System.out.println("Nhap kich thuoc ma tran: ");
            System.out.print("So dong: ");
            M = Integer.parseInt(sc.nextLine());
            System.out.print("So cot: ");
            N = Integer.parseInt(sc.nextLine());
        } while (M <= 0 || N <= 0);

        double[][] P = inputMatrix(M, N);

        System.out.println("Ma tran: ");
        for (double[] doubles : P) {
            for (int j = 0; j < P[0].length; j++) {
                System.out.print(doubles[j] + "\t");
            }
            System.out.println();
        }

        System.out.println();

        System.out.println("H(X) = " + entropy(P, 'X'));
        System.out.println("H(Y) = " + entropy(P, 'Y'));
        System.out.println("H(X\\Y) = " + conditionalEntropy(P, 'X', 'Y'));
        System.out.println("H(Y\\X) = " + conditionalEntropy(P, 'Y', 'X'));
        System.out.println("H(X,Y) = " + jointEntropy(P));
        System.out.println("H(Y) - H(Y\\X) = " + (entropy(P, 'Y') - conditionalEntropy(P, 'Y', 'X')));
        System.out.println("I(X;Y) = " + mutalInformation(P));
        System.out.println("D(P(X)||P(Y)) = " + divergence(P, 'X', 'Y'));
        System.out.println("D(P(Y)||P(X)) = " + divergence(P, 'Y', 'X'));
    }

    public static double[][] inputMatrix(int M, int N) {
        double[][] P = new double[M][N];

        System.out.println("Nhap ma tran: ");

        for (int j = 0; j < N; j++) {
            for (int i = 0; i < M; i++) {
                do {
                    System.out.print("P(" + j + ", " + i + "): ");
                    String inputString = sc.nextLine();
                    if (inputString.contains("/")) {
                        String[] arr = inputString.split("/");
                        Double tuSo = Double.parseDouble(arr[0]);
                        Double mauSo = Double.parseDouble(arr[1]);
                        P[j][i] = tuSo / mauSo;
                    } else {
                        P[j][i] = Double.parseDouble(inputString);
                    }
                } while (P[j][i] < 0 || P[j][i] > 1);
            }
        }
        return P;
    }

    // Tinh H(X), H(Y)
    public static double entropy(double[][] P, char character) {
        double entropy = 0.0;

//        int row = P.length;
        int col = P[0].length;

        if (character == 'X') {
            for (double[] doubles : P) {
                double sum_hang = 0.0;
                for (int j = 0; j < col; j++) {
                    sum_hang += doubles[j];
                }
                entropy += sum_hang * Math.log(sum_hang) / Math.log(2.0);
            }
        } else if (character == 'Y') {
            for (int i = 0; i < col; i++) {
                double sum_cot = 0.0;
                for (double[] doubles : P) {
                    sum_cot += doubles[i];
                }
                entropy += sum_cot * (Math.log(sum_cot) / Math.log(2.0));
            }
        }

        return -entropy;
    }

    // Tinh H(X\Y) va H(Y\X)
    public static double conditionalEntropy(double[][] P, char character, char conditional) {
        double conditionalEntropy = 0.0;

        int row = P.length;
        int col = P[0].length;
        double[] Px = new double[row];
        double[] Py = new double[col];

        for (int i = 0; i < row; i++) {
            double sum_hang = 0.0;
            for (int j = 0; j < col; j++) {
                sum_hang += P[i][j];
            }
            Px[i] = sum_hang;
        }

        for (int i = 0; i < col; i++) {
            double sum_cot = 0.0;
            for (double[] doubles : P) {
                sum_cot += doubles[i];
            }
            Py[i] = sum_cot;
        }

        if (character == 'X' && conditional == 'Y') {

            for (double[] doubles : P) {
                for (int j = 0; j < col; j++) {
                    double xsdk = doubles[j] / Py[j];
                    if (xsdk == 0.0) {
                        continue;
                    }
                    conditionalEntropy += doubles[j] * (Math.log(xsdk) / Math.log(2.0));
                }
            }

        } else if (character == 'Y' && conditional == 'X') {
            for (int i = 0; i < col; i++) {
                for (int j = 0; j < row; j++) {
                    double xsdk = P[j][i] / Px[j];
                    if (xsdk == 0.0) {
                        continue;
                    }
                    conditionalEntropy += P[j][i] * (Math.log(xsdk) / Math.log(2.0));
                }
            }
        }

        return -conditionalEntropy;
    }

    // Tinh H(X,Y)
    public static double jointEntropy(double[][] P) {
        return entropy(P, 'Y') + conditionalEntropy(P, 'X', 'Y');
    }

    // Tinh I(X;Y)
    public static double mutalInformation(double[][] P) {
        return entropy(P, 'X') + entropy(P, 'Y') - jointEntropy(P);
    }

    // Tinh D(P(X)||P(Y)) va D(P(Y)||P(X))
    public static double divergence(double[][] P, char character1, char character2) {
        double divergence = 0.0;

        int row = P.length;
        int col = P[0].length;
        double[] Px = new double[row];
        double[] Py = new double[col];

        for (int i = 0; i < row; i++) {
            double sum_hang = 0.0;
            for (int j = 0; j < col; j++) {
                sum_hang += P[i][j];
            }
            Px[i] = sum_hang;
        }

        for (int i = 0; i < col; i++) {
            double sum_cot = 0.0;
            for (double[] doubles : P) {
                sum_cot += doubles[i];
            }
            Py[i] = sum_cot;
        }

        if (character1 == 'X' && character2 == 'Y') {
            for (int i = 0; i < row; i++) {
                divergence += Px[i] * (Math.log(Px[i] / Py[i]) / Math.log(2.0));
            }
        } else if (character1 == 'Y' && character2 == 'X') {
            for (int i = 0; i < col; i++) {
                divergence += Py[i] * (Math.log(Py[i] / Px[i]) / Math.log(2.0));
            }
        }

        return divergence;
    }
}