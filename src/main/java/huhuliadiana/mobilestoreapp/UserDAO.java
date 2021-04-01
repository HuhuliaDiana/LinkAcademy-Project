package huhuliadiana.mobilestoreapp;


import org.springframework.data.repository.CrudRepository;

public interface UserDAO extends CrudRepository<User, Integer> {
    User findByUsername(String username);
    User findById(int id);


}
