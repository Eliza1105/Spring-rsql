package rsql.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.context.annotation.Description;

import javax.persistence.*;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
            @Column(name = "id")
    Long id;
    @Column(name = "surname", nullable = false)
    String surname;
    @Column(name ="name", nullable = false)
    String name;
    @Column(name = "patronymic")
    String patronymic;
    @Column(name = "login")
    String login;
    @Column(name = "password")
    String password;
}
