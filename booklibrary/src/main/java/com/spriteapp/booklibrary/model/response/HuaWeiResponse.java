package com.spriteapp.booklibrary.model.response;

/**
 * Created by userfirst on 2018/3/1.
 */

public class HuaWeiResponse {
    /**
     * return_code : success
     * code : 10000
     * amount : 4990
     * private_key : MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCK6ZBjmEN8Oo3m/pTP4FtaC0fkn/VPc5WczR3Pfxai+Z0yOf/jJ3RUkEsVEXDrBHn0Pr+kKx3YoGhDbevtyMGJj3GEaqCMMcdP+GHlU9jEGqOPaqILp/AJ0YuEjCwTkNZ0mvR0SScQ7VgBmfo5x7riG0UcZaiZqWScyMwAkb7Z0iG6c+f/enHbhIMqk50uuFLYcp6duaYrXbB8yaWB4urWeshBFrwB+Vta87C29AaoIJN3L3d0dCWUlqGC4uO/3W/9VET/hSYYbGEt0aQ+qJAQiohdeQRQd0OfzgTJ9yzLHQQv2G6T8vIs7UjM6U5aqNkFH3Vdfxoio7u3tAbAizJHAgMBAAECggEAAULbl7vjKPaTITzAdx6vVaLylSXKoV0IR7MpCSA7QWGIxtL1+sb57Gae9+L/pnKU9N24+bGWhMf4ClyuHquK7kfKHQymxn8RMy2rHdzkA+vF/mBWdkqhCEXJwXtQTyEqgUaC36dmXmX5PZJ7Thj9Hl5nAzn9DOAX1MCR8ZNhKzMUvQwu+lch5/v7krQdj4Cte7cFJGsuWjbjAOxdMAg1Y/jtOm58G9TwvH7QIiZDC/pnLdoCRuPfUqNE+1o5Hd3s8FAympNDM15UI7D5sZzS5G936o1yRsBJl5bt8mMHG8M4oM9ubONRagM7uClVE8RjCLqq9M4IrkpEd1fQJ5i4GQKBgQDXOzx0V6FOypt6IGt3WWrlWRu0pcqxFNlhn9BjtTyk+8DTiWBDpe84bgQLo5BoZMP0oGB7lv1LZyTuVTiDvOPGssN6soZDnRBlt9B5JC4OGVCKSZW3igRstDpU6QicDuznKD5BCErPBBw6TPUWyM0hAs9uSS0MidmxxTODezl29QKBgQClOYyhd5zLrLWYcteTtjjIHJhfqzWeAcMH9AySFg1Z/5dzDr9F4kHTJDPIzYSpxtNNaXGQWHO/tnCSpFOxSmEBY+RLeV5/QJ4Fx0XXz9J9+1+0ELhZKuKOsNaTWI2LqYuWxwnxLEBRDdzJ636n0qBt9Kw8qFTbHjCLMMS1bxumywKBgQDRP2rX5TYTJxEfG5O/VbSCQy6XYzP7Ez0AF2J+/X13HLbsMbvgKNu47eSK+Dr18zk7hAwpO7MH8sBJ4ZpvIV4FGFjJ2fSEmZeOfLV2gQExfdT6jO28obcJg/LOCDzxk+UoGszjZs9sEh/rYlRJEXG0bUoDe6sLbJ2zofgiFNPaXQKBgEwVgzf7n71+y6TnBXSkzeMAn/42FhurzgbEkGFZPE68Tx6RpOzmcs9q9Vm7oiKYR0d3je8dfDfFKHOurkeO67q7KC7FNgm9VMduazAlKFYRQpHlvS56fBefQUBa39t3eKs2/tILQZbRnmFxS0K20F+x8vp/vYuljE8cZwjxoDcfAoGBAMVd3B2+FXRmcj8ToQ6QQi3maePqW/CX9/ht3ZkADdgP872QYoDxkm0hddi57DbtECe03PkbtRExdpVhbQ4wbJaxPDDgI1OlivlcaJLqwDzL0XsM1CnOypdCCUaqgi+8sz0j1mCWK285NYmitw1MclOaa1H3ph5dyR68LO3DYpMK
     * order_no : 2401490529020180301144412640
     * total_fee : 4990
     * pay_type : huaweipay
     */

    private String return_code;
    private int code;
    private int amount;
    private String private_key;
    private String order_no;
    private int total_fee;
    private String pay_type;

    public String getReturn_code() {
        return return_code;
    }

    public void setReturn_code(String return_code) {
        this.return_code = return_code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getPrivate_key() {
        return private_key;
    }

    public void setPrivate_key(String private_key) {
        this.private_key = private_key;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public int getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(int total_fee) {
        this.total_fee = total_fee;
    }

    public String getPay_type() {
        return pay_type;
    }

    public void setPay_type(String pay_type) {
        this.pay_type = pay_type;
    }
}
