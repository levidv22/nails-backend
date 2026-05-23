//package upeu.edu.pe.nails.controllers;
//
//import org.springframework.web.bind.annotation.*;
//import upeu.edu.pe.nails.entities.Notification;
//import upeu.edu.pe.nails.repositories.NotificationRepository;
//
//import java.util.List;
//
//@RestController
//@RequestMapping(path = "/api/notifications")
//public class NotificationController {
//
//    private final NotificationRepository notificationRepository;
//
//    public NotificationController(NotificationRepository notificationRepository) {
//        this.notificationRepository = notificationRepository;
//    }
//
//    @GetMapping(path = "/my-notifications")
//    public List<Notification> getMyNotifications(@RequestParam Long userId) {
//
//        if (userId == null) {
//            throw new RuntimeException("El userId es obligatorio");
//        }
//
//        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
//    }
//
//    @PutMapping("/read/{id}")
//    public Notification markAsRead(@PathVariable Long id) {
//
//        Notification notification = notificationRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
//
//        notification.setRead(true);
//
//        return notificationRepository.save(notification);
//    }
//}