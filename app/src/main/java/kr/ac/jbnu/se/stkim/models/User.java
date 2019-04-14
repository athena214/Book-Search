package kr.ac.jbnu.se.stkim.models;

/**
 * author : Hyunmyung
 */
public class User {
    private String id;
    private String name;
    private String pw;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }


    /**
    * 테스트로 만든 메소드임
     *
     * @param a 파라미터 1번임
     * @param b 파라미터 2번임
     * @return 둘이 합친거임
     */
    public String metthod(String a, String b) {
        return a + b;
    }
}
