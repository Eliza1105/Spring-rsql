package rsql.controller;


import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;
import rsql.CustomRsqlVisitor;
import rsql.model.User;
import rsql.repository.UserRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserRepository repository;

    @GetMapping("/")
    List<User> all() {
        return repository.findAll();
    }

  /*  @GetMapping("/search")
    List<User> search(@RequestParam(value = "search") String search) {
        Node rootNode = new RSQLParser().parse(search);
        Specification<User> spec = rootNode.accept(new CustomRsqlVisitor<>());
        return repository.findAll(spec);
    }

    @GetMapping("/{id}")
    Optional<User> one(@PathVariable Integer id) {
        return repository.findById(id);
    }

    @PostMapping("/")
    User create(@RequestBody User user) {
        return repository.save(user);
    }

    @PutMapping("/{id}")
    User update(@RequestBody User user, @PathVariable Integer id) {
        return repository.save(user); //check if exists first ...
    }

    @DeleteMapping("/{id}")
    void delete(@PathVariable Integer id) {
        repository.deleteById(id);
    }

   */
    @GetMapping("/filter")
    public List<User> filter(@RequestParam(value = "search") String search){
        Node rootNode = new RSQLParser().parse(search);
        Specification<User> spec = rootNode.accept(new CustomRsqlVisitor<User>());
        return repository.findAll(spec);
    }
    @RequestMapping(method = RequestMethod.GET, value = "/filter2")
    @ResponseBody
    public List<User> findByRsql(@RequestParam(value = "search", required = false) String search) {
        System.out.println(search);
        if (search != null|| search.isEmpty()) {
            Node rootNode = new RSQLParser().parse(search);
            Specification<User> spec = rootNode.accept(new CustomRsqlVisitor<User>());
            return repository.findAll(spec);
        } else return repository.findAll();
    }
    //http://localhost:8080/users?page=10&size=10

    @GetMapping(value = "/users", params = {"page", "size"})
    public Page<User> paginationUsers(@RequestParam("page") int page,
                                      @RequestParam("size") int size) throws IOException {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAll(pageable);
    }
    //http://localhost:8080/users/filter?page=1&size=10&search=id>100
    @GetMapping(value = "/users/filter", params = {"page", "size", "search"})
    public Page<User> paginationFilterUsers(@RequestParam("page") int page,
                                            @RequestParam("size") int size,
                                            @RequestParam(value = "search", required = false) String search) {
        Pageable pageable = PageRequest.of(page, size);
        if (search != null) {
            Node rootNode = new RSQLParser().parse(search);
            Specification<User> spec = rootNode.accept(new CustomRsqlVisitor<User>());
            return repository.findAll(spec, pageable);
        } else return repository.findAll(pageable);
    }
    //http://localhost:8080/users/filter?page=1&size=10&search=id>100&sort=surname
    @GetMapping(value = "/users/filter", params = {"page", "size", "search", "sort"})
    public Page<User> paginationFilterUsers(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "sort", required = false) String sort) {

        Sort sortOrder = Sort.unsorted();
        if (sort != null) {
            String[] sortParams = sort.split(",");
            for (String param : sortParams) {
                String[] sortInfo = param.split(":");
                        String property = sortInfo[0];
                Sort.Direction direction = sortInfo.length > 1 && sortInfo[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
                sortOrder = sortOrder.and(
                        Sort.by(direction, property));
            }
        }

        Pageable pageable = PageRequest.of(page, size, sortOrder);

        if (search != null) {
            Node rootNode = new RSQLParser().parse(search);
            Specification<User> spec = rootNode.accept(new CustomRsqlVisitor<User>());
            return repository.findAll(spec, pageable);
        } else {
            return repository.findAll(pageable);
        }
    }

    //http://localhost:8080/filter?search=id>2;id<=5
    //http://localhost:8080/filter?search=name=in=(,)-g70b2byjpmyb1c8n0d
    //http://localhost:8080/filter?search=name>Эдуард; name<Яков
    //http://localhost:8080/filter?search=name==**-2c9a4h
}
