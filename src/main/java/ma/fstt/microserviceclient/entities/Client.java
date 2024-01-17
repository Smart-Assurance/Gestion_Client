package ma.fstt.microserviceclient.entities;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.TypeAlias;

import java.util.Date;

@Document(collection = "users")
@TypeAlias("CLIENT")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class Client extends User {

    @Size(max = 200)
    private String add_wallet_cli;

    @NotBlank
    @Size(max = 12)
    private String cin;

    @NotBlank
    private Date date_of_birth;

    public Client(String l_name, String f_name, String username, String password, String email,
                  String phone, String city, String address,
                  String add_wallet_cli, String cin, Date date_of_birth) {
        super(null, l_name, f_name, username, password, email, phone, city, address, "CLIENT");
        this.add_wallet_cli = add_wallet_cli;
        this.cin = cin;
        this.date_of_birth = date_of_birth;
    }

    public Client(String l_name, String f_name, String email,
                  String phone, String city, String address,
                  String add_wallet_cli, String cin, Date date_of_birth) {
        super(null, l_name, f_name, l_name+"_"+f_name, date_of_birth.toString(), email, phone, city, address, "CLIENT");
        this.add_wallet_cli = add_wallet_cli;
        this.cin = cin;
        this.date_of_birth = date_of_birth;
    }


}