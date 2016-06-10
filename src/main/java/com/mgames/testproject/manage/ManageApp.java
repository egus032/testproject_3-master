package com.mgames.testproject.manage;

import com.mgames.adminframework.ManageSettingsProvider;
import com.mgames.adminframework.MgAdminApp;
import com.mgames.adminframework.MgAdminModule;
import com.mgames.adminframework.db.handlers.AdminUserDBHandler;
import com.mgames.adminframework.modules.MgUsersModule;
import com.mgames.testproject.Strings;
import com.mgames.testproject.Variables;
import com.mgames.testproject.managers.UsersManager;
import com.mgames.testproject.resolvers.FileSaverResolver;
import com.mgames.utils.db.MgDbPool;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.annotations.Theme;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Component.Listener;
import com.vaadin.ui.UI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@StyleSheet("http://fonts.googleapis.com/css?family=Open+Sans:400,300,600,700,800&subset=latin,cyrillic")
@Component("manageApp")
@Scope("prototype")
@Theme("mgames")
public class ManageApp extends MgAdminApp {

	private static final Logger log = LoggerFactory.getLogger(ManageApp.class.getName());

	public static ManageSettingsProvider getSettings() {
		return ((ManageApp) UI.getCurrent()).getManageSettingsProvider();
	}

	@Value(value = "${appName}")
	private String appName;

	@Autowired
	private transient MgDbPool pool;

	@Autowired
	private Strings strings;

	@Autowired
	private UsersManager usersManager;

	@Autowired
	private Variables vars;

	@Autowired
	private FileSaverResolver fileSaverResolver;

	private MgUsersModule getUsersModule() {
		LinkedHashMap<String, String> usersColumns = new LinkedHashMap<String, String>() {
			{
				put("id", "id");
				put("social_id", "Соц. ID");
				put("fname", "Имя");
				put("lname", "Фамилия");
				put("last_visit", "Последнее посещение");
				put("reg_date", "Дата регистрации");
			}
		};

		MgUsersModule usersModule = new MgUsersModule(pool,
													  usersColumns,
													  true, false, false, true,
													  null, e -> usersManager.clear()
		);
		return usersModule;
	}

	@Override
	protected List<MgAdminModule> getCustomModules() {
		List<MgAdminModule> modules = new ArrayList<>();

		switch (getCurrentUser().getRole()) {
			case ADMIN:
				modules.add(getUsersModule());
				break;
			case DEVELOPER:
				break;
			case USER:
				modules.add(getUsersModule());
				break;
		}

		return modules;
	}

	@Override
	protected ManageSettingsProvider getManageSettings() {
		Listener stringsListener = (Event event) -> {
			strings.reload();
		};
		Listener varsListener = (Event event) -> {
			vars.reload();
		};
		return new ManageSettingsProvider(appName,
										  pool,
										  stringsListener,
										  varsListener,
										  () -> vars.RESOURCES_FOLDER,
										  () -> vars.RESOURCES_HTTP_PATH,
										  new AdminUserDBHandler(pool));
	}

}
