package huhuliadiana.mobilestoreapp;

import org.springframework.data.repository.CrudRepository;


public interface OrdersDataDAO extends CrudRepository<OrdersData, Integer> {

    OrdersData findByCartId(int cartId);

}
