package cn.com.gf.sdk;

class Main {
    public static void main(String[] args) {
        try {
            Pandora instance = Pandora.getInstance();
            String result = instance.encode(InfoType.PHONE, "13751761288");

            String phone = instance.decode(result);
            System.out.println(phone);
        } catch (PandoraException e) {
            System.out.println(e.getMessage() + ", " + e.getCause());
        }
    }
}
