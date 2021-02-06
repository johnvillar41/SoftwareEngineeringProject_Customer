package emp.project.softwareengineeringprojectcustomer.Presenter;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

import emp.project.softwareengineeringprojectcustomer.Interface.IRegister;
import emp.project.softwareengineeringprojectcustomer.Models.Bean.CustomerModel;
import emp.project.softwareengineeringprojectcustomer.SendEmail;

public class RegisterPresenter implements IRegister.IRegisterPresenter {

    private IRegister.IRegisterView view;
    private IRegister.IRegisterService service;
    private CustomerModel model;

    public RegisterPresenter(IRegister.IRegisterView view, CustomerModel model, IRegister.IRegisterService service) {
        this.view = view;
        this.model = model;
        this.service = service;
    }

    public static final String CUSTOMER_STATUS_PENDING = "Pending";

    @Override
    public void onRegisterButtonClicked(List<String> arrTexts, InputStream FILE_INPUT_STREAM) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                view.displayLoadingCircle();
                if (view.displayErrors()) {
                    try {
                        model = new CustomerModel(arrTexts.get(0),
                                arrTexts.get(1),
                                arrTexts.get(2),
                                CUSTOMER_STATUS_PENDING,
                                arrTexts.get(3), FILE_INPUT_STREAM);
                        service.insertCustomerToDB(model);
                        SendEmail sendEmail = new SendEmail(arrTexts.get(3));
                        sendEmail.sendMailCode();
                        view.hideLoadingCircler();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        view.hideLoadingCircler();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                        view.hideLoadingCircler();
                    }
                }
                view.onSuccess();
                view.hideLoadingCircler();
            }
        });
        thread.start();

    }

    @Override
    public void onImageButtonClicked() {
        view.loadImageFromGallery();
    }
}
