package com.nimscreation.MyLinkedin.ConnectionService.services;

import com.nimscreation.MyLinkedin.ConnectionService.auth.AuthContextHolder;
import com.nimscreation.MyLinkedin.ConnectionService.entity.Person;
import com.nimscreation.MyLinkedin.ConnectionService.exception.BadRequestException;
import com.nimscreation.MyLinkedin.ConnectionService.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConnectionService {

    private final PersonRepository personRepository;

    public List<Person> getFirstDegreeConnectionsOfUser(Long userId) {
        log.info("Getting first degree connections of user with ID: {}", userId);

        return personRepository.getFirstDegreeConnections(userId);
    }

    public void sendConnectionRequest(Long receiverId) {

        Long senderId = AuthContextHolder.getCurrentUserId();

        log.info("sending connection request with senderId: {}, receiverId: {}", senderId, receiverId);

        if (senderId.equals(receiverId)) {
            throw new BadRequestException("Both sender and receiver are the same");
        }

        // Check if request is already sent
        boolean alreadySentRequest = personRepository.connectionRequestExists(senderId, receiverId);

        if (alreadySentRequest) {
            throw new BadRequestException("Connection request already exists, cannot send again");
        }

        // Check if both users are already connected
        boolean alreadyConnected = personRepository.alreadyConnected(senderId, receiverId);

        if (alreadyConnected) {
            throw new BadRequestException("Already connected users, cannot add connection request");
        }

        // Check if receiver has already sent a request to sender
        boolean reverseRequestExists = personRepository.connectionRequestExists(receiverId, senderId);

        if (reverseRequestExists) {
            throw new BadRequestException(
                    "This user has already sent you a connection request. Please accept or reject it."
            );
        }

        // Create connection request
        personRepository.addConnectionRequest(senderId, receiverId);

        log.info("Successfully sent the connection request");
    }

    public void acceptConnectionRequest(Long senderId) {
        Long receiverId = AuthContextHolder.getCurrentUserId();
        log.info("Accepting a connection request with senderId: {}, receiverId: {}", senderId, receiverId);

        if (senderId.equals(receiverId)) {
            throw new BadRequestException("Both sender and receiver are the same");
        }

        boolean alreadyConnected = personRepository.alreadyConnected(senderId, receiverId);
        if (alreadyConnected) {
            throw new BadRequestException("Already connected users, cannot accept connection request again");
        }

        boolean alreadySentRequest = personRepository.connectionRequestExists(senderId, receiverId);
        if (!alreadySentRequest) {
            throw new BadRequestException("No Connection request exists, cannot accept without request");
        }

        personRepository.acceptConnectionRequest(senderId, receiverId);

        log.info("Successfully accepted the connection request with senderId: {}, receiverId: {}", senderId,
                receiverId);

    }

    public void rejectConnectionRequest(Long senderId) {
        Long receiverId = AuthContextHolder.getCurrentUserId();
        log.info("Rejecting a connection request with senderId: {}, receiverId: {}", senderId, receiverId);

        if (senderId.equals(receiverId)) {
            throw new BadRequestException("Both sender and receiver are the same");
        }

        boolean alreadySentRequest = personRepository.connectionRequestExists(senderId, receiverId);
        if (!alreadySentRequest) {
            throw new BadRequestException("No Connection request exists, cannot reject it");
        }

        personRepository.rejectConnectionRequest(senderId, receiverId);

        log.info("Successfully rejected the connection request with senderId: {}, receiverId: {}", senderId,
                receiverId);
    }
}
