package br.com.agdev;

public class Main {

    public static void main(String[] args) {
        CopyService copyService = new CopyService();
        copyService.copy("/run/user/1000/gvfs/mtp:host=SAMSUNG_SAMSUNG_Android_RQCR5004KZL/Armazenamento interno", "/home/als/Desktop/S21+");
    }
}
