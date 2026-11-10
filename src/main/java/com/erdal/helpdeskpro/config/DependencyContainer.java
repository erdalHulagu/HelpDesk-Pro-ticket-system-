package com.erdal.helpdeskpro.config;

import org.hibernate.SessionFactory;
import com.erdal.helpdeskpro.repository.SecurityLogRepository;
import com.erdal.helpdeskpro.repository.dao.SecurityLogDAO;
import com.erdal.helpdeskpro.service.SecurityLogService;
import com.erdal.helpdeskpro.service.impl.SecurityLogServiceImpl;
import com.erdal.helpdeskpro.auth.AuthController;
import com.erdal.helpdeskpro.auth.AuthService;
import com.erdal.helpdeskpro.auth.AuthServiceImpl;
import com.erdal.helpdeskpro.auth.TokenService;
import com.erdal.helpdeskpro.authorization.Authorization;
import com.erdal.helpdeskpro.authorization.AuthorizationImpl;
import com.erdal.helpdeskpro.config.util.HibernateUtil;
import com.erdal.helpdeskpro.controller.CommentController;
import com.erdal.helpdeskpro.controller.TicketController;
import com.erdal.helpdeskpro.controller.UserController;
import com.erdal.helpdeskpro.controller.AnnouncementController;
import com.erdal.helpdeskpro.controller.AssetController;
import com.erdal.helpdeskpro.mapper.CommentMapper;
import com.erdal.helpdeskpro.repository.CommentRepository;
import com.erdal.helpdeskpro.repository.TicketRepository;
import com.erdal.helpdeskpro.repository.UserRepository;
import com.erdal.helpdeskpro.repository.AuthTokenRepository;
import com.erdal.helpdeskpro.repository.AnnouncementRepository;
import com.erdal.helpdeskpro.repository.AssetRepository;
import com.erdal.helpdeskpro.repository.dao.CommentDAO;
import com.erdal.helpdeskpro.repository.dao.TicketDAO;
import com.erdal.helpdeskpro.repository.dao.UserDAO;
import com.erdal.helpdeskpro.repository.dao.AuthTokenDAO;
import com.erdal.helpdeskpro.repository.dao.AnnouncementDAO;
import com.erdal.helpdeskpro.repository.dao.AssetDAO;
import com.erdal.helpdeskpro.service.CommentService;
import com.erdal.helpdeskpro.service.TicketService;
import com.erdal.helpdeskpro.service.UserService;
import com.erdal.helpdeskpro.service.AnnouncementService;
import com.erdal.helpdeskpro.service.impl.CommentServiceImpl;
import com.erdal.helpdeskpro.service.impl.TicketServiceImpl;
import com.erdal.helpdeskpro.service.impl.UserServiceImpl;
import com.erdal.helpdeskpro.service.impl.AnnouncementServiceImpl;
import com.erdal.helpdeskpro.controller.AttachmentController;
import com.erdal.helpdeskpro.repository.AttachmentRepository;
import com.erdal.helpdeskpro.repository.dao.AttachmentDAO;
import com.erdal.helpdeskpro.repository.dao.TicketActivityDAO;
import com.erdal.helpdeskpro.service.AttachmentService;
import com.erdal.helpdeskpro.service.impl.AttachmentServiceImpl;
import com.erdal.helpdeskpro.controller.NotificationController;
import com.erdal.helpdeskpro.repository.NotificationRepository;
import com.erdal.helpdeskpro.repository.dao.NotificationDAO;
import com.erdal.helpdeskpro.service.NotificationService;
import com.erdal.helpdeskpro.service.impl.NotificationServiceImpl;
import com.erdal.helpdeskpro.repository.TicketActivityRepository;
import com.erdal.helpdeskpro.service.TicketActivityService;
import com.erdal.helpdeskpro.service.impl.TicketActivityServiceImpl;
import com.erdal.helpdeskpro.controller.TicketActivityController;
import com.erdal.helpdeskpro.repository.AnnouncementReadRepository;
import com.erdal.helpdeskpro.repository.dao.AnnouncementReadDAO;
import com.erdal.helpdeskpro.service.AnnouncementReadService;
import com.erdal.helpdeskpro.service.impl.AnnouncementReadServiceImpl;
import com.erdal.helpdeskpro.controller.AnnouncementReadController;
import com.erdal.helpdeskpro.service.ExcelExportService;
import com.erdal.helpdeskpro.service.impl.ExcelExportServiceImpl;
import com.erdal.helpdeskpro.service.AssetService;
import com.erdal.helpdeskpro.service.impl.AssetServiceImpl;
import com.erdal.helpdeskpro.repository.AssetAssignmentRepository;
import com.erdal.helpdeskpro.repository.dao.AssetAssignmentDAO;
import com.erdal.helpdeskpro.service.AssetAssignmentService;
import com.erdal.helpdeskpro.service.impl.AssetAssignmentServiceImpl;
import com.erdal.helpdeskpro.controller.AssetAssignmentController;
import com.erdal.helpdeskpro.repository.AssetMaintenanceRepository;
import com.erdal.helpdeskpro.repository.dao.AssetMaintenanceDAO;
import com.erdal.helpdeskpro.service.AssetMaintenanceService;
import com.erdal.helpdeskpro.service.impl.AssetMaintenanceServiceImpl;
import com.erdal.helpdeskpro.controller.AssetMaintenanceController;

public final class DependencyContainer {

	private static final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

	// Repositories
	private static final UserRepository userRepository = new UserDAO(sessionFactory);

	private static final TicketRepository ticketRepository = new TicketDAO(sessionFactory);

	private static final CommentRepository commentRepository = new CommentDAO(sessionFactory);

	private static final NotificationRepository notificationRepository = new NotificationDAO(sessionFactory);

	private static final Authorization authorization = new AuthorizationImpl();

	private static final TicketActivityRepository ticketActivityRepository = new TicketActivityDAO(sessionFactory);

	private static final AnnouncementRepository announcementRepository = new AnnouncementDAO(sessionFactory);

	private static final AnnouncementReadRepository announcementReadRepository = new AnnouncementReadDAO(
			sessionFactory);

	private static final SecurityLogRepository securityLogRepository = new SecurityLogDAO(sessionFactory);
	
	private static final AssetAssignmentRepository assetAssignmentRepository =
	        new AssetAssignmentDAO(sessionFactory);
	
	private static final AssetMaintenanceRepository assetMaintenanceRepository =
	        new AssetMaintenanceDAO(sessionFactory);

	

	// Asset Repository
	private static final AssetRepository assetRepository = new AssetDAO(sessionFactory);

	// Services
	private static final UserService userService = new UserServiceImpl(userRepository);

	private static final TicketActivityService ticketActivityService = new TicketActivityServiceImpl(
			ticketActivityRepository, ticketRepository, authorization);

	private static final TicketService ticketService = new TicketServiceImpl(ticketRepository, authorization,
			userRepository, ticketActivityService);

	private static final NotificationService notificationService = new NotificationServiceImpl(notificationRepository,
			userRepository, ticketRepository);

	private static final CommentService commentService = new CommentServiceImpl(commentRepository, ticketRepository,
			userRepository, authorization, notificationService, ticketActivityService);

	private static final AnnouncementService announcementService = new AnnouncementServiceImpl(announcementRepository);

	private static final AnnouncementReadService announcementReadService = new AnnouncementReadServiceImpl(
			announcementReadRepository, announcementRepository);

	private static final SecurityLogService securityLogService = new SecurityLogServiceImpl(securityLogRepository);

	private static final ExcelExportService excelExportService = new ExcelExportServiceImpl();

	private static final AssetService assetService = new AssetServiceImpl(assetRepository);
	
	private static final AssetAssignmentService assetAssignmentService =
	        new AssetAssignmentServiceImpl(assetAssignmentRepository);
	
	private static final AssetMaintenanceService assetMaintenanceService =
	        new AssetMaintenanceServiceImpl(assetMaintenanceRepository);

	

	// Token repository
	private static final AuthTokenRepository authTokenRepository = new AuthTokenDAO(sessionFactory);

	// Authentication
	private static final TokenService tokenService = new TokenService(authTokenRepository, userRepository);

	private static final AuthService authService = new AuthServiceImpl(userService, tokenService, securityLogService);

	// Controllers
	private static final UserController userController = new UserController(userService);

	private static final TicketController ticketController = new TicketController(ticketService);

	private static final AuthController authController = new AuthController(authService);

	private static final NotificationController notificationController = new NotificationController(
			notificationService);

	private static final TicketActivityController ticketActivityController = new TicketActivityController(
			ticketActivityService);

	private static final AnnouncementController announcementController = new AnnouncementController(
			announcementService);

	private static final AnnouncementReadController announcementReadController = new AnnouncementReadController(
			announcementReadService);

	private static final AssetController assetController = new AssetController(assetService);
	
	private static final AssetAssignmentController assetAssignmentController =
	        new AssetAssignmentController(assetAssignmentService);
	
	private static final AssetMaintenanceController assetMaintenanceController =
	        new AssetMaintenanceController(assetMaintenanceService);

	// Attachments
	private static final AttachmentRepository attachmentRepository = new AttachmentDAO(sessionFactory);

	private static final CommentMapper commentMapper = new CommentMapper(attachmentRepository);

	private static final CommentController commentController = new CommentController(commentService, commentMapper);

	private static final AttachmentService attachmentService = new AttachmentServiceImpl(attachmentRepository,
			ticketRepository, authorization, commentRepository);

	private static final AttachmentController attachmentController = new AttachmentController(attachmentService);

	private DependencyContainer() {
		// Bu sınıftan nesne oluşturulmasını engeller.
	}

	public static UserController getUserController() {
		return userController;
	}

	public static TicketController getTicketController() {
		return ticketController;
	}

	public static CommentController getCommentController() {
		return commentController;
	}

	public static AuthController getAuthController() {
		return authController;
	}

	public static AuthService getAuthService() {
		return authService;
	}

	public static UserRepository getUserRepository() {
		return userRepository;
	}

	public static AssetRepository getAssetRepository() {
		return assetRepository;
	}

	public static AttachmentController getAttachmentController() {
		return attachmentController;
	}

	public static Authorization getAuthorization() {
		return authorization;
	}

	public static NotificationController getNotificationController() {
		return notificationController;
	}

	public static NotificationService getNotificationService() {
		return notificationService;
	}

	public static TicketActivityController getTicketActivityController() {
		return ticketActivityController;
	}

	public static AnnouncementController getAnnouncementController() {
		return announcementController;
	}

	public static AnnouncementReadController getAnnouncementReadController() {
		return announcementReadController;
	}

	public static SecurityLogService getSecurityLogService() {
		return securityLogService;
	}

	public static ExcelExportService getExcelExportService() {
		return excelExportService;
	}

	public static UserService getUserService() {
		return userService;
	}

	public static TicketService getTicketService() {
		return ticketService;
	}

	public static AssetService getAssetService() {
		return assetService;
	}
	public static AssetController getAssetControlller() {
		return assetController;
	}
	
	public static AssetAssignmentController getAssetAssignmentController() {
	    return assetAssignmentController;
	}
	
	public static AssetMaintenanceController getAssetMaintenanceController() {
	    return assetMaintenanceController;
	}
	
	
}
