public class WypozyczoneKlasa {
    String name= null;
    String id = null;

    WypozyczoneKlasa(String name,String id){
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return getName() + " wypożyczył: " + getId();
    }

}
