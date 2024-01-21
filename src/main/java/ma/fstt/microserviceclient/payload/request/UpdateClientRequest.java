package ma.fstt.microserviceclient.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor


public class UpdateClientRequest {
    @NotBlank
    private String l_name;
    @NotBlank
    private String f_name;
    @NotBlank
    @Indexed(unique = true)
    private String email;
    @NotBlank
    @Indexed(unique = true)
    private String username;
    @NotBlank
    private String phone;
    @NotBlank
    private String city;
    @NotBlank
    private String address;
    @NotBlank
    @Size(max = 12)
    private String cin;
    @NotBlank
    private Date date_of_birth;
    @NotBlank
    private String add_wallet_cli;
}