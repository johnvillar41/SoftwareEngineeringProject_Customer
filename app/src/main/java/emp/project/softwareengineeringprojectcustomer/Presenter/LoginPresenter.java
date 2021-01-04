package emp.project.softwareengineeringprojectcustomer.Presenter;

import android.content.Context;

import java.lang.ref.WeakReference;
import java.sql.SQLException;

import emp.project.softwareengineeringprojectcustomer.Interface.ILogin;
import emp.project.softwareengineeringprojectcustomer.Models.CustomerModel;
import emp.project.softwareengineeringprojectcustomer.Service.LoginService;
import emp.project.softwareengineeringprojectcustomer.Views.LoginActivityView;

public class LoginPresenter implements ILogin.ILoginPresenter {
    private ILogin.ILoginView view;
    private ILogin.ILoginService service;
    private CustomerModel model;
    private WeakReference<LoginActivityView> weakReference_Context;

    public LoginPresenter(ILogin.ILoginView view, CustomerModel model, LoginActivityView weakReference_Context) {
        this.view = view;
        this.model = model;
        this.service = LoginService.getInstance();
        this.weakReference_Context = new WeakReference<>(weakReference_Context);
    }

    private final static String USER_NOT_FOUND = "User not found!";
    private final static String EMPTY_FIELD = "One or more fields are empty!";

    @Override
    public void onLoginButtonClicked(String username, String password) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    weakReference_Context.get().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            view.displayProgressLoader();
                        }
                    });
                    if (model.validateLogin(username, password)) {
                        if (service.fetchCustomerLoginCredentials(username, password)) {
                            view.onSuccess();
                        } else {
                            weakReference_Context.get().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    view.onError(USER_NOT_FOUND);
                                    view.hideProgressLoader();
                                }
                            });
                        }
                    } else {
                        weakReference_Context.get().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                view.onError(EMPTY_FIELD);
                                view.hideProgressLoader();
                            }
                        });
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

    }
}
