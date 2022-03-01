import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class MainFrame extends JFrame implements Runnable{
    private JMenuBar menuBar;
    private JMenuItem iPokaz,iWypozycz,iOddaj;
    private JPanel p1;
    private JTextField name;
    private JComboBox<String> id;
    private JButton addButton;

    ArrayList<WypozyczoneKlasa> list = new ArrayList<>();
    ArrayList<String> listaGier = wypelnijListeGier();
    ArrayList<String> listaGierWypozyczonych = wypelnijListeGierWypozyczonych();

    public MainFrame(String title)  {
        super(title);

        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension dim = tk.getScreenSize();
        setSize(new Dimension(dim.width / 2, dim.height / 2));
        addWindowListener(new WindowClosingAdapter());
        iPokaz = new JMenuItem("Pokaz wszystkie wypozyczone gry");
        iWypozycz = new JMenuItem("Wypozycz gre");
        iOddaj = new JMenuItem("Oddaj gre");

        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        p1 = new JPanel();
        p1.setSize(dim.height, dim.height);
        p1.setBackground(Color.white);
        add(p1);

        menuBar.add(iPokaz);
        menuBar.add(iWypozycz);
        menuBar.add(iOddaj);

        setJMenuBar(menuBar);
        iPokaz.addActionListener(e -> {

            p1.removeAll();
            try{
                zaladujWypozyczenia();
                iPokaz.updateUI();
                WypozyczoneKlasa list2[] = new WypozyczoneKlasa[list.size()];
                list.toArray(list2);
                JList<WypozyczoneKlasa> jlist = new JList<>(list2);
                JScrollPane jsp;

                jsp = new JScrollPane(jlist,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
                jsp.setPreferredSize(new Dimension(250,300));
                p1.add(jsp);
                jsp.updateUI();
                setVisible(true);



            } catch (Exception ex){
                ex.printStackTrace();
            }
        });


        iWypozycz.addActionListener(e -> {
            p1.removeAll();
            SwingUtilities.updateComponentTreeUI(this);
            name = new JTextField(10);
            id = new JComboBox<>();
            for (String item:listaGier) {
                id.addItem(item);
            }
            id.updateUI();
            addButton = new JButton("Dodaj");
            p1.add(name);
            p1.add(id);
            p1.add(addButton);


            addButton.addActionListener(

                    addButton->{

                        try {
                            if(dodajWypozyczenie(sprawdz(name.getText()), id.getSelectedItem().toString())){
                                usunGreZListy(id.getSelectedItem().toString());
                                JOptionPane.showMessageDialog(null, "Udalo sie!");

                            } else {
                                JOptionPane.showMessageDialog(null, "Wystapil blad!");
                            }
                        } catch (pusty pusty) {
                            pusty.printStackTrace();
                        }
                        listaGier = wypelnijListeGier();
                        listaGierWypozyczonych = wypelnijListeGierWypozyczonych();
                    });
        });

        iOddaj.addActionListener(e -> {
            p1.removeAll();
            SwingUtilities.updateComponentTreeUI(this);
            name = new JTextField(10);
            id = new JComboBox<>();
            for (String item:listaGierWypozyczonych) {
                id.addItem(item);
            }
            id.updateUI();
            addButton = new JButton("Oddaj");
            p1.add(name);
            p1.add(id);
            p1.add(addButton);
            addButton.addActionListener(
                    addButton->{
                        if(oddajGre(name.getText(),id.getSelectedItem().toString())){
                            JOptionPane.showMessageDialog(null, "Udalo sie!");

                        } else {
                            JOptionPane.showMessageDialog(null, "Wystapil blad!");
                        }
                        listaGier = wypelnijListeGier();
                        listaGierWypozyczonych = wypelnijListeGierWypozyczonych();

                    });
        });

    }


    public static void main(String[] args)   {
        EventQueue.invokeLater(new MainFrame("Wypożyczalnia gier"));
    }


    @Override
    public void run() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }


    class WindowClosingAdapter extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            int odp= JOptionPane.showConfirmDialog(null, "Czy na pewno wyjsc?", "Pytanie", JOptionPane.YES_NO_OPTION);
            if(odp == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
            if(odp == JOptionPane.NO_OPTION) {
                setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            }

            if(odp == JOptionPane.CANCEL_OPTION) {
                setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            }
        }
    }

    public String sprawdz(String name) throws pusty{
        if(name.isEmpty()){
            throw new pusty();
        }
        else {
            return name;
        }
    }
    public boolean dodajWypozyczenie(String name,String id){
        try {
            WypozyczoneKlasa nowy = new WypozyczoneKlasa(name,id);
            list.add(nowy);

            FileWriter writer = new FileWriter("wypozyczone.txt",true);
            BufferedWriter bw = new BufferedWriter(writer);
            bw.write(name +" " + id);
            bw.newLine();
            bw.close();

            FileWriter writer2 = new FileWriter("wypozyczonegry.txt",true);
            BufferedWriter bw2 = new BufferedWriter(writer2);
            bw2.newLine();
            bw2.write(id);
            bw2.close();

            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public void usunGreZListy(String id){
        ArrayList<String> listaGier = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(
                    "gry.txt"));

            String strLine;
            String gra;
            while ((strLine = br.readLine()) != null) {
                gra = strLine;
                listaGier.add(gra);
            }
            for(int x = 0; x < listaGier.size(); x++){
                if(listaGier.get(x).contains(id)){

                    listaGier.remove(x);
                }
            }
            FileWriter writer = new FileWriter("gry.txt");
            for(String a: listaGier){
                writer.write(a + System.lineSeparator());
            }
            writer.close();


        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public boolean oddajGre(String name,String id){
        ArrayList<String> listaGier = new ArrayList<>();
        ArrayList<String> listaGier1 = new ArrayList<>();
        try {
            String delete = id;
            BufferedReader reader = new BufferedReader(new FileReader(
                    "wypozyczonegry.txt"));
            String gra;
            String strLine1;
            while ((strLine1 = reader.readLine()) != null) {
                gra = strLine1;
                listaGier.add(gra);
            }
            for(int x = 0; x < listaGier.size(); x++){
                if(listaGier.get(x).contains(delete)){

                    listaGier.remove(x);
                }
            }
            FileWriter writer = new FileWriter("wypozyczonegry.txt");
            for(String a: listaGier){
                writer.write(a + System.lineSeparator());
            }
            writer.close();

            BufferedReader reader2 = new BufferedReader(new FileReader("gry.txt"));
            String gryDoWypozyczenia;
            String strLine2;
            while((strLine2 = reader2.readLine()) != null){
                gryDoWypozyczenia = strLine2;
                listaGier1.add(gryDoWypozyczenia);
            }
            listaGier1.add(id);
            FileWriter writer2 = new FileWriter("gry.txt");
            for(String b: listaGier1){
                writer2.write(b + System.lineSeparator());
            }
            writer2.close();

            Map<String, String> map = FileToHashMap();
            map.remove(name);
            BufferedWriter writer1 = new BufferedWriter(new FileWriter("wypozyczone.txt"));
            for(Map.Entry<String, String> entry : map.entrySet()){
                writer1.write(entry.getKey() + " " + entry.getValue());
                writer1.newLine();
            }
            writer1.flush();
            writer1.close();


            return true;
        }


        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public void zaladujWypozyczenia(){
        list.clear();
        try{
            BufferedReader br = new BufferedReader(new FileReader(
                    "wypozyczone.txt"));

            String strLine;
            String id, name;
            while ((strLine = br.readLine()) != null) {
                String[] f1 = strLine.split(" ");
                id = f1[0];
                name = f1[1];
                WypozyczoneKlasa a = new WypozyczoneKlasa(name,id);
                list.add(a);
            }
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }



    public ArrayList<String> wypelnijListeGier() {

        ArrayList<String> listaGier = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(
                    "gry.txt"));

            String strLine;
            String gra;

            while ((strLine = br.readLine()) != null) {
                gra = strLine;
                listaGier.add(gra);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return listaGier;
    }

    public ArrayList<String> wypelnijListeGierWypozyczonych() {

        ArrayList<String> listaGier = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(
                    "wypozyczonegry.txt"));

            String strLine;
            String gra;
            while ((strLine = br.readLine()) != null) {
                gra = strLine;
                listaGier.add(gra);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return listaGier;
    }
    public Map<String, String> FileToHashMap(){
        String a = " ";
        Map<String, String> map = new HashMap<>();
        try(Stream<String> lines = Files.lines(Paths.get("wypozyczone.txt"))){
            lines.filter(line -> line.contains(a)).forEach(line -> map.putIfAbsent(line.split(a)[0], line.split(a)[1]));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

}


class pusty extends Exception{
    public pusty(){
        JOptionPane.showMessageDialog(null, "Wprowadzono niepoprawna wartosc!!!!!");
    }
}