package Assignment3.Firstapp.Stock;
public class Stock {
    private String name;
    private String price;

    public Stock(String name, String price){
        this.name = name;
        this.price = price;
    }

    public String getPrice() {
        return price;
    }

    public String getNameString() {
        return name;
    }
}
