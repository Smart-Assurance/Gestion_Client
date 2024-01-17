package ma.fstt.microserviceclient.controllers;


import ma.fstt.microserviceclient.entities.Client;
import ma.fstt.microserviceclient.payload.request.AddClientRequest;
import ma.fstt.microserviceclient.payload.response.MessageResponse;

import ma.fstt.microserviceclient.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/clients")

public class ClientController {

    @Autowired
    PasswordEncoder encoder;
    @Autowired
    public ClientRepository clientRepository;

    @PostMapping("/add")
    public ResponseEntity<MessageResponse> addClient(@RequestBody AddClientRequest addClientRequest) {
        try {
            Client client = new Client(
                    addClientRequest.getL_name(),
                    addClientRequest.getF_name(),
                    addClientRequest.getL_name()+"_"+addClientRequest.getF_name(),
                    encoder.encode(addClientRequest.getDate_of_birth().toString()),
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
    public ResponseEntity<List<Client>> getAllClients() {
        try {
            List<Client> clients = clientRepository.findAll();
            return ResponseEntity.ok(clients);
        } catch (Exception e) {
            return ResponseEntity.status(500).build(); // Erreur interne du serveur
        }
    }


    @GetMapping("/{clientId}")
    public ResponseEntity<Client> getClientById(@PathVariable String clientId) {
        try {
            Optional<Client> client = clientRepository.findById(clientId);
            if (client.isPresent()) {
                return ResponseEntity.ok(client.get());
            } else {
                return ResponseEntity.status(404).build(); // Ressource non trouvée
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build(); // Erreur interne du serveur
        }
    }





    @DeleteMapping("/{clientId}")
    public ResponseEntity<MessageResponse> deleteClient(@PathVariable String clientId) {
        try {
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
}