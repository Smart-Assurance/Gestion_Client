package ma.fstt.microserviceclient.controllers;


import ma.fstt.microserviceclient.entities.Client;
import ma.fstt.microserviceclient.payload.request.AddClientRequest;
import ma.fstt.microserviceclient.payload.request.UpdateClientRequest;
import ma.fstt.microserviceclient.payload.response.MessageResponse;

import ma.fstt.microserviceclient.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/clients")

public class ClientController {

    @Autowired
    PasswordEncoder encoder;
    @Autowired
    public ClientRepository clientRepository;

    private final AuthService authService;
    public ClientController(ClientRepository clientRepository, AuthService authService) {
        this.clientRepository = clientRepository;
        this.authService = authService;
    }

    public String encodeDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        return encoder.encode(sdf.format(date));
    }
    @PostMapping("/add")
    public ResponseEntity<MessageResponse> addClient(@RequestBody AddClientRequest addClientRequest,@RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Extract the token from the Authorization header
            String token = extractTokenFromHeader(authorizationHeader);
            if (!authService.isValidEmployeeToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(401, "Not authorized"));
            }
            Client client = new Client(
                    addClientRequest.getL_name(),
                    addClientRequest.getF_name(),
                    addClientRequest.getL_name()+"_"+addClientRequest.getF_name(),
                    encodeDate(addClientRequest.getDate_of_birth()),
                    addClientRequest.getEmail(),
                    addClientRequest.getPhone(),
                    addClientRequest.getCity(),
                    addClientRequest.getAddress(),
                    addClientRequest.getAdd_wallet_cli(),
                    addClientRequest.getCin(),
                    addClientRequest.getDate_of_birth()
            );

            clientRepository.save(client);

            return ResponseEntity.ok(new MessageResponse(201,"Client saved successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(400,"Client doesn't save "));

        }
    }
    @GetMapping("/getAll")
    public ResponseEntity<Object> getAllClients(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Extract the token from the Authorization header
            String token = extractTokenFromHeader(authorizationHeader);
            if (!authService.isValidEmployeeToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(401, "Not authorized"));
            }
            List<Client> clients = clientRepository.findAll();
            List<Client> filteredClients = clients.stream()
                    .filter(client -> hasRole(client, "ROLE_CLIENT"))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(filteredClients);
        } catch (Exception e) {
            return ResponseEntity.status(500).build(); // Erreur interne du serveur
        }
    }
    private boolean hasRole(Client client, String role) {
        return client.getRole().equals(role);
    }

    @GetMapping("/{clientId}")
    public ResponseEntity<Object> getClientById(@PathVariable String clientId,@RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Extract the token from the Authorization header
            String token = extractTokenFromHeader(authorizationHeader);
            if (!authService.isValidEmployeeToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(401, "Not authorized"));
            }
            Optional<Client> client = clientRepository.findById(clientId);
            if (client.isPresent() && hasRole(client.get(), "ROLE_CLIENT")) {
                return ResponseEntity.ok(client.get());
            } else {
                return ResponseEntity.status(404).build(); // Ressource non trouvée
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build(); // Erreur interne du serveur
        }
    }





    @DeleteMapping("/{clientId}")
    public ResponseEntity<MessageResponse> deleteClient(@PathVariable String clientId,@RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Extract the token from the Authorization header
            String token = extractTokenFromHeader(authorizationHeader);
            if (!authService.isValidEmployeeToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(401, "Not authorized"));
            }
            Optional<Client> client = clientRepository.findById(clientId);
            if (client.isPresent()) {
                clientRepository.delete(client.get());
                return ResponseEntity.ok(new MessageResponse(200, "Client deleted successfully"));
            } else {
                return ResponseEntity.status(404).body(new MessageResponse(404, "Client not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new MessageResponse(500, "Internal server error"));
        }
    }

    //update client
    @PutMapping("/{clientId}")
    public ResponseEntity<MessageResponse> updateClient(
            @PathVariable String clientId,
            @RequestBody UpdateClientRequest updatedClientRequest,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            // Extract the token from the Authorization header
            String token = extractTokenFromHeader(authorizationHeader);
            if (!authService.isValidEmployeeToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(401, "Not authorized"));
            }
            Optional<Client> optionalClient = clientRepository.findById(clientId);
            if (optionalClient.isPresent()) {
                Client client = optionalClient.get();

                // Mettre à jour tous les champs de l'employé
                client.setL_name(updatedClientRequest.getL_name());
                client.setF_name(updatedClientRequest.getF_name());
                client.setUsername(updatedClientRequest.getUsername());
                client.setEmail(updatedClientRequest.getEmail());
                client.setPhone(updatedClientRequest.getPhone());
                client.setCity(updatedClientRequest.getCity());
                client.setAddress(updatedClientRequest.getAddress());
                client.setCin(updatedClientRequest.getCin());
                client.setDate_of_birth(updatedClientRequest.getDate_of_birth());
                client.setAdd_wallet_cli(updatedClientRequest.getAdd_wallet_cli());

                clientRepository.save(client);
                return ResponseEntity.ok(new MessageResponse(200, "Client updated successfully"));
            } else {
                return ResponseEntity.status(404).body(new MessageResponse(404, "Client not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new MessageResponse(500, "Internal server error"));
        }
    }

    private String extractTokenFromHeader(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }



}