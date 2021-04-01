package huhuliadiana.mobilestoreapp;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class UserController {

    private static Connection conn;
    private String dbURL_admin = "jdbc:mysql://127.0.0.1:3307/dhuhulia_mobile_store_app_admin?useSSL=false";

    private static void createConnection(String dbURL) throws SQLException {
        String dbUser = "root";
        String dbPassword = "";
        conn = DriverManager.getConnection(dbURL, dbUser, dbPassword);
    }


    @GetMapping("/users")
    @ResponseBody
    public Iterable<User> users() {
        return userDAO.findAll();
    }

    @GetMapping("submit")
    public ModelAndView submit() {
        return new ModelAndView("submit.html");
    }

    @GetMapping("")
    public ModelAndView firstPage() {
        return new ModelAndView("firstPage.html");
    }

    @GetMapping("register")
    public ModelAndView register() {
        return new ModelAndView("register.html");
    }

    @PostMapping("/sign-out")
    public ModelAndView signOut(@RequestParam("user_id") int user_id) throws SQLException {

        User user=userDAO.findById(user_id);
        user.setRegistered(false);
        userDAO.save(user);
        return new ModelAndView("redirect:/");
    }

    @PostMapping("/go-to-submit")
    public ModelAndView goToSubmit() {
        return new ModelAndView("redirect:/submit");
    }

    @Autowired
    private UserDAO userDAO;

    @PostMapping("/submit-action")
    public ModelAndView registerAction(@RequestParam("firstname") String firstname,
                                       @RequestParam("lastname") String lastname,
                                       @RequestParam("username") String username,
                                       @RequestParam("password") String password) {

        boolean usernameFound=false;

        for (User user1 : userDAO.findAll()) {
            if (user1.getUsername().equals(username)) {
                usernameFound = true;
                break;
            }
        }

        if(usernameFound) {
            return new ModelAndView("redirect:/submit");
        }
        else {
            User user = new User();
            user.setLastname(lastname);
            user.setFirstname(firstname);
            user.setUsername(username);
            user.setPassword(password);
            userDAO.save(user);
            return new ModelAndView("redirect:/register");
        }


    }


    @PostMapping("/register-action")
    public ModelAndView loginAction(@RequestParam("username") String username,
                                    @RequestParam("password") String password) throws SQLException {


        User user = userDAO.findByUsername(username);
        if (user.getPassword().equals(password)) {

            user.setRegistered(true);
            userDAO.save(user);
            return new ModelAndView("redirect:/dashboard/" + userDAO.findByUsername(username).getId());

        } else {
            return new ModelAndView("redirect:/register");
        }
    }


    @GetMapping("dashboard/{user_id}")
    public ModelAndView dashboard(@PathVariable("user_id") int user_id) throws SQLException {


        if(!userDAO.findById(user_id).isRegistered())
        {
            return new ModelAndView("redirect:/register");
        }

        else {
            ModelAndView mav = new ModelAndView("dashboard.html");

            RestTemplate restTemplate = new RestTemplate();
            int numberCartProducts = 0;

            try {
                ResponseEntity<Product[]> responseEntity = restTemplate.getForEntity("http://localhost:3081/products",
                        Product[].class);

                Product[] allProducts = responseEntity.getBody();

                mav.addObject("products", allProducts);
                numberCartProducts = allProducts.length;

            } catch (Exception e) {
                mav.addObject("products", new Product[0]);

            }

            mav.addObject("user_id", user_id);

            mav.addObject("numberCartProducts", numberCartProducts);

            mav.addObject("btnPrice", "0");
            mav.addObject("input_btnPrice", "0");
            mav.addObject("btnBrand", "0");
            mav.addObject("btnMemRAM", "0");

            return mav;
        }
    }


    @PostMapping("/dashboard/buy")
    public ModelAndView buy(@RequestParam("id") int id,
                            @RequestParam("user_id") int user_id,
                            @RequestParam("btnPrice") String btnPrice,
                            @RequestParam("btnBrand") String btnBrand,
                            @RequestParam("btnMemRAM") String btnMemRAM) throws SQLException {

        RestTemplate restTemplate = new RestTemplate();

        Cart newCart = new Cart();
        newCart.setUserId(user_id);

        //transmit cart-ul cu userId-ul primit anterior
        newCart = restTemplate.postForObject("http://localhost:3081/carts", newCart, Cart.class);

        createConnection(dbURL_admin);
        Statement sqlStmt1 = conn.createStatement();
        sqlStmt1.execute("update product set quantity=quantity-1 where id=" + id);
        sqlStmt1.close();
        conn.close();

        Product product = restTemplate.getForObject("http://localhost:3081/products/" + id, Product.class);

        //verific daca acest produs nu se afla in cos deja. Daca da, cantitatea pt el creste cu o unitate
        List<CartProduct> listCartProduct = newCart.getCartProductList();

        int gasit = 0;
        if (listCartProduct != null) {
            for (CartProduct p : listCartProduct) {
                if (gasit == 0 && p.getProduct_id()==id)//am gasit un produs identic in cos
                {
                    createConnection(dbURL_admin);
                    Statement sqlStmt2 = conn.createStatement();
                    sqlStmt2.execute("update cart_product set quantity=quantity+1 where product_id=" + id);
                    sqlStmt2.close();
                    conn.close();
                    gasit = 1;
                }

            }
        }

        if (gasit == 0) {

            CartProduct cartProduct = new CartProduct();

            cartProduct.setBattery_capacity(product.getBattery_capacity());
            cartProduct.setDiagonal(product.getDiagonal());
            cartProduct.setMemory_RAM(product.getMemory_RAM());
            cartProduct.setPhoto(product.getPhoto());
            cartProduct.setPrice(product.getPrice());
            cartProduct.setInternet_speed(product.getInternet_speed());
            cartProduct.setMain_camera(product.getMain_camera());
            cartProduct.setBrand(product.getBrand());
            cartProduct.setModel(product.getModel());
            cartProduct.setProduct_id(product.getId());

            restTemplate.postForObject("http://localhost:3081/carts/" + newCart.getId() + "/products",
                    cartProduct, CartProduct.class);
        }


        if (!btnPrice.equals("0") || !btnBrand.equals("0")
        || !btnMemRAM.equals("0")){

            return new ModelAndView("redirect:/dashboard/" + user_id+"/filters/"+
                    btnMemRAM+"/"+btnBrand+"/"+btnPrice);
        } else {
            return new ModelAndView("redirect:/dashboard/" + user_id);
        }


    }

    @GetMapping("my-cart/{user_id}")
    public ModelAndView goToCart(@PathVariable("user_id") int user_id) throws SQLException {

        if(!userDAO.findById(user_id).isRegistered())
        {
            return new ModelAndView("redirect:/register");
        }
        else {
            RestTemplate restTemplate = new RestTemplate();

            //aflu id-ul cart-ului cu userId-ul dat
            createConnection(dbURL_admin);
            Statement sqlStmt1 = conn.createStatement();
            Statement sqlStmt2 = conn.createStatement();
            ResultSet rs = sqlStmt1.executeQuery("select id from cart where user_id=" + user_id);
            ResultSet rs1 = sqlStmt2.executeQuery("select count(id) from cart where user_id=" + user_id);

            if (rs1.next() && rs1.getInt(1) != 0) {
                int cartId= 0;
                while (rs.next()) {
                    cartId = rs.getInt(1);//ultimul cartID al user-ului
                }

                ModelAndView mav = new ModelAndView("cart.html");
                mav.addObject("user_id", user_id);
                mav.addObject("cartId", cartId);


                CartProduct[] cart_products=new CartProduct[0];

                //afisez cosul numai daca acesta nu exista in bd a comenzilor efectuate
                if (ordersDataDAO.findByCartId(cartId) == null) {
                    ResponseEntity<CartProduct[]> responseEntity =
                            restTemplate.getForEntity("http://localhost:3081/carts/" + cartId + "/products", CartProduct[].class);
                    cart_products = responseEntity.getBody();
                }

                float pret = 0;
                for (CartProduct c : cart_products) {
                    pret += c.getQuantity() * c.getPrice();
                }


                mav.addObject("pretTotal", pret);
                mav.addObject("cart_products", cart_products);

                rs.close();
                rs1.close();
                sqlStmt1.close();
                sqlStmt2.close();
                conn.close();

                return mav;

            } else {
                ModelAndView mav = new ModelAndView("cart.html");
                mav.addObject("pretTotal", 0.0f);
                return mav;
            }
        }
    }

    @PostMapping("/go-to-cart")
    public ModelAndView myCart(@RequestParam("user_id") int user_id) {
        return new ModelAndView("redirect:/my-cart/" +user_id);
    }

    @PostMapping("/remove-product")
    public ModelAndView scoateProdus(@RequestParam("product_id") int product_id,
                                     @RequestParam("user_id") int user_id,
                                     @RequestParam("cartId") int cartId) throws SQLException {

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<CartProduct[]> responseEntity =
                restTemplate.getForEntity("http://localhost:3081/carts/" + cartId + "/products",
                        CartProduct[].class);

        CartProduct[] cart_products = responseEntity.getBody();

        createConnection(dbURL_admin);
        Statement sqlStmt1 = conn.createStatement();

        for (CartProduct c : cart_products) {
            if (c.getProduct_id() == product_id) {
                if (c.getQuantity() == 1) {
                    sqlStmt1.execute("delete from cart_product where product_id=" + product_id);
                } else {
                    sqlStmt1.execute("update cart_product set quantity=quantity-1 where product_id=" + product_id);
                }
                sqlStmt1.close();
                conn.close();

                createConnection(dbURL_admin);
                Statement sqlStmt2 = conn.createStatement();
                sqlStmt2.execute("update product set quantity=quantity+1 where id=" + product_id);
                sqlStmt2.close();
                conn.close();
            }
        }
        return new ModelAndView("redirect:/my-cart/" + user_id);
    }




    @PostMapping("/send-command")
    public ModelAndView sendCommand(@RequestParam("user_id") int user_id,
                                    @RequestParam("pretTotalTransmis") float pretTotalTransmis,
                                    @RequestParam(value = "cod_voucher", required = false) String cod_voucher) throws SQLException {


        User user=userDAO.findById(user_id);
        user.setVoucher_used(cod_voucher);
        userDAO.save(user);

        return new ModelAndView("redirect:/" + user_id + "/process-order/" + pretTotalTransmis);
    }


    @GetMapping("/{user_id}/process-order/{pretTotalTransmis}")
    public ModelAndView processOrder(@PathVariable("user_id") int user_id,
                                     @PathVariable("pretTotalTransmis") float pretTotalTransmis) throws SQLException {

        if(!userDAO.findById(user_id).isRegistered())
        {
            return new ModelAndView("redirect:/register");
        }
        else {
            User myUser = userDAO.findById(user_id);
            ModelAndView mav = new ModelAndView("order.html");

            //id-ul ultimului cart al user-ului
            createConnection(dbURL_admin);
            Statement sqlStmt1 = conn.createStatement();
            ResultSet rs1 = sqlStmt1.executeQuery("select max(id) from cart where user_id=" + myUser.getId());
            int cartId = 0;
            if (rs1.next()) {
                cartId = rs1.getInt(1);
            }
            rs1.close();
            sqlStmt1.close();
            conn.close();

            mav.addObject("firstname", myUser.getFirstname());
            mav.addObject("lastname", myUser.getLastname());

            mav.addObject("pretTotalTransmis", pretTotalTransmis);
            mav.addObject("cartId", cartId);
            mav.addObject("user_id", user_id);

            float costLivrare = 0;
            if (pretTotalTransmis < 2999.9f) {
                costLivrare = 16.5f;
            }
            mav.addObject("costLivrare", costLivrare);

            float pretFinal = pretTotalTransmis + costLivrare;
            mav.addObject("pretFinal", pretFinal);

            return mav;
        }
    }


    @GetMapping("{user_id}/order-completed")
    public ModelAndView orderCompleted(@PathVariable("user_id") int user_id) throws SQLException {

        if(!userDAO.findById(user_id).isRegistered())
        {
            return new ModelAndView("redirect:/register");
        }
        else {
            ModelAndView mav = new ModelAndView("finalPage.html");
            mav.addObject("user_id",user_id);
            return mav;
        }
    }

    @Autowired
    private OrdersDataDAO ordersDataDAO;

    @PostMapping("/back-to-dashboard")
    public ModelAndView backToDashboard(@RequestParam("user_id") int user_id) {
        return new ModelAndView("redirect:/dashboard/" + user_id);
    }

    @PostMapping("/finish-order")
    public ModelAndView finishOrder(
            @RequestParam("lastname") String lastname,
            @RequestParam("firstname") String firstname,
            @RequestParam("address") String address,
            @RequestParam("phone") String phone,
            @RequestParam("town") String town,
            @RequestParam("county") String county,
            @RequestParam("cartId") int cartId,
            @RequestParam("pretTotalTransmis") float pretTotalTransmis,
            @RequestParam("user_id") int user_id) throws SQLException {

        OrdersData ordersData = new OrdersData();

        ordersData.setAddress(address);
        ordersData.setCounty(county);
        ordersData.setLastname(lastname);
        ordersData.setFirstname(firstname);
        ordersData.setTown(town);
        ordersData.setPhone(phone);
        ordersData.setCartId(cartId);

        // calcul pretTotal
        if (pretTotalTransmis < 2999.99f) {
            pretTotalTransmis += 16.5f;
        }
        ordersData.setTotal_price(pretTotalTransmis);

        ordersData.setUser_id(user_id);

        ordersDataDAO.save(ordersData);
        return new ModelAndView("redirect:/" + user_id+ "/order-completed");

    }

    @GetMapping("/orders-data")
    @ResponseBody
    public Iterable<OrdersData> ordersData() {
        return ordersDataDAO.findAll();
    }

    @PostMapping("/apply-filters")
    public ModelAndView applyFilters(@RequestParam("user_id") int user_id,
                                     @RequestParam("btnMemRAM") String btnMemRAM,
                                     @RequestParam("btnBrand") String btnBrand,
                                     @RequestParam("btnPrice") String btnPrice)
    {
        return new ModelAndView("redirect:/dashboard/" + user_id+"/filters/"+
                btnMemRAM+"/"+btnBrand+"/"+btnPrice);
    }

    @GetMapping("/dashboard/{user_id}/filters/{btnMemRAM}/{btnBrand}/{btnPrice}")
    public ModelAndView userAppliedFilters(@PathVariable("user_id") int user_id,
                                           @PathVariable("btnMemRAM") String btnMemRAM,
                                           @PathVariable("btnBrand") String btnBrand,
                                           @PathVariable("btnPrice") String btnPrice) throws SQLException {

        if(!userDAO.findById(user_id).isRegistered())
        {
            return new ModelAndView("redirect:/register");
        }
        else {
            ModelAndView mav = new ModelAndView("dashboard.html");
            mav.addObject("user_id",user_id);
            mav.addObject("btnBrand", btnBrand);
            mav.addObject("btnMemRAM", btnMemRAM);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Product[]> responseEntity =
                    restTemplate.getForEntity("http://localhost:3081/products", Product[].class);

            List<Product> productsBrand = new ArrayList<>();

            //btnBrand

            switch (btnBrand) {
                case "SAMSUNG":
                    for (Product p : responseEntity.getBody()) {
                        if (p.getBrand().equals("SAMSUNG")) {
                            productsBrand.add(p);
                        }
                    }

                    break;
                case "HUAWEI":
                    for (Product p : responseEntity.getBody()) {
                        if (p.getBrand().equals("HUAWEI")) {
                            productsBrand.add(p);
                        }
                    }
                    break;
                case "XIAOMI":
                    for (Product p : responseEntity.getBody()) {
                        if (p.getBrand().equals("XIAOMI")) {
                            productsBrand.add(p);
                        }
                    }
                    break;
                case "LENOVO":
                    for (Product p : responseEntity.getBody()) {
                        if (p.getBrand().equals("LENOVO")) {
                            productsBrand.add(p);
                        }
                    }
                    break;
                case "ASUS":
                    for (Product p : responseEntity.getBody()) {
                        if (p.getBrand().equals("ASUS")) {
                            productsBrand.add(p);
                        }
                    }
                    break;
                default:
                    productsBrand.addAll(Arrays.asList(responseEntity.getBody()));
                    break;
            }

            //btnMemRAM

            List<Product> productsMemRAM = new ArrayList<>();

            switch (btnMemRAM) {
                case "16GB":
                    for (Product p : productsBrand) {
                        if (p.getMemory_RAM().equals("16GB")) {
                            productsMemRAM.add(p);
                        }
                    }
                    break;
                case "6GB":
                    for (Product p : productsBrand) {
                        if (p.getMemory_RAM().equals("6GB")) {
                            productsMemRAM.add(p);
                        }
                    }
                    break;
                case "8GB":
                    for (Product p : productsBrand) {
                        if (p.getMemory_RAM().equals("8GB")) {
                            productsMemRAM.add(p);
                        }
                    }
                    break;
                case "12GB":
                    for (Product p : productsBrand) {
                        if (p.getMemory_RAM().equals("12GB")) {
                            productsMemRAM.add(p);
                        }
                    }
                    break;
                default:
                    productsMemRAM.addAll(productsBrand);
                    break;
            }

            //btnPrice

            List<Product> productsPrice = new ArrayList<>();
            createConnection(dbURL_admin);
            Statement sqlStmt = conn.createStatement();
            ResultSet rs;

            if (btnPrice.equals("decreasing-price"))
            {
                mav.addObject("input_btnPrice", "Preț descrescător");

                if (!btnBrand.equals("0") && !btnMemRAM.equals("0")) {
                    rs = sqlStmt.executeQuery("select* from product where memory_ram='" + btnMemRAM +
                            "' and brand='" + btnBrand + "' order by price desc");
                } else if (btnBrand.equals("0") && !btnMemRAM.equals("0")) {
                    rs = sqlStmt.executeQuery("select* from product where memory_ram='" + btnMemRAM +
                            "' order by price desc");
                } else if (!btnBrand.equals("0") && btnMemRAM.equals("0")) {
                    rs = sqlStmt.executeQuery("select* from product where brand='" + btnBrand +
                            "' order by price desc");
                } else {
                    rs = sqlStmt.executeQuery("select* from product order by price desc");
                }

                while (rs.next()) {
                    Product product = new Product();

                    product.setId(rs.getInt(1));
                    product.setQuantity(rs.getInt(11));
                    product.setBattery_capacity(rs.getString(2));
                    product.setDiagonal(rs.getString(4));
                    product.setPhoto(rs.getString(9));
                    product.setMemory_RAM(rs.getString(7));
                    product.setModel(rs.getString(8));
                    product.setPrice(rs.getFloat(10));
                    product.setBrand(rs.getString(3));
                    product.setInternet_speed(rs.getString(5));
                    product.setMain_camera(rs.getString(6));

                    productsPrice.add(product);
                }
                rs.close();
                sqlStmt.close();
                conn.close();

            } else if (btnPrice.equals("rising-price"))
            {
                mav.addObject("input_btnPrice", "Preț crescător");

                if (!btnBrand.equals("0") && !btnMemRAM.equals("0")) {
                    rs = sqlStmt.executeQuery("select* from product where brand='" + btnBrand +
                            "' and memory_ram='" + btnMemRAM + "' order by  price");
                } else if (btnBrand.equals("0") && !btnMemRAM.equals("0")) {
                    rs = sqlStmt.executeQuery("select* from product where memory_ram='" + btnMemRAM +
                            "' order by price");
                } else if (!btnBrand.equals("0") && btnMemRAM.equals("0")) {
                    rs = sqlStmt.executeQuery("select* from product where brand='" + btnBrand +
                            "' order by price");
                } else {
                    rs = sqlStmt.executeQuery("select* from product order by price");
                }

                while (rs.next()) {
                    Product product = new Product();

                    product.setId(rs.getInt(1));
                    product.setQuantity(rs.getInt(11));
                    product.setBattery_capacity(rs.getString(2));
                    product.setDiagonal(rs.getString(4));
                    product.setPhoto(rs.getString(9));
                    product.setMemory_RAM(rs.getString(7));
                    product.setModel(rs.getString(8));
                    product.setPrice(rs.getFloat(10));
                    product.setBrand(rs.getString(3));
                    product.setInternet_speed(rs.getString(5));
                    product.setMain_camera(rs.getString(6));

                    productsPrice.add(product);
                }
                rs.close();
                sqlStmt.close();
                conn.close();
            } else {
                mav.addObject("input_btnPrice", "0");
                productsPrice.addAll(productsMemRAM);
            }

            mav.addObject("products", productsPrice);
            int numberCartProducts = productsPrice.size();
            mav.addObject("numberCartProducts", numberCartProducts);

            return mav;
        }

    }

}
