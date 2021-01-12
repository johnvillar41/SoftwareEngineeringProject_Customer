package emp.project.softwareengineeringprojectcustomer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import emp.project.softwareengineeringprojectcustomer.Interface.IRegister;
import emp.project.softwareengineeringprojectcustomer.Models.Bean.CustomerModel;
import emp.project.softwareengineeringprojectcustomer.Presenter.RegisterPresenter;


public class RegisterPresenterTest {
    IRegister.IRegisterView view;
    IRegister.IRegisterService service;
    IRegister.IRegisterPresenter presenter;

    @Before
    public void setUp() {
        view = new MockRegisterView();
        service = new MockRegisterService();
        presenter = new RegisterPresenter(view, new CustomerModel(), service);
    }

    @Test
    public void testSuccess() throws InterruptedException {
        presenter.onRegisterButtonClicked(
                "sample",
                "sample",
                "sample",
                "sample",
                "sample",
                new InputStream() {
                    @Override
                    public int read() {
                        return 0;
                    }
                });
        Thread.sleep(1000);
        Assert.assertTrue(MockRegisterView.pass_success);
    }

    @Test
    public void testErrorOnEnterAllFields() throws InterruptedException {
        presenter.onRegisterButtonClicked("",
                "",
                "",
                "",
                "",
                new InputStream() {
                    @Override
                    public int read() {
                        return 0;
                    }
                });
        Thread.sleep(1000);
        Assert.assertTrue(MockRegisterView.pass_enter_all_fields);
    }

    @Test
    public void testErrorOnPasswordNotEqual() throws InterruptedException {
        presenter.onRegisterButtonClicked("asd",
                "as",
                "dsa",
                "asd",
                "asd",
                new InputStream() {
                    @Override
                    public int read() {
                        return 0;
                    }
                });
        Thread.sleep(1000);
        Assert.assertTrue(MockRegisterView.pass_password_not_equal);
    }

    @Test
    public void testErrorOnEmptyImage() throws InterruptedException {
        presenter.onRegisterButtonClicked("asd",
                "as",
                "dsa",
                "asd",
                "asd",
                null);
        Thread.sleep(1000);
        Assert.assertTrue(MockRegisterView.pass_empty_image);
    }

    @Test
    public void testDisplayPhoneGallery() throws InterruptedException {
        presenter.onImageButtonClicked();
        Thread.sleep(1000);
        Assert.assertTrue(MockRegisterView.isGalleryDisplaying);
    }

    static class MockRegisterView implements IRegister.IRegisterView {
        static boolean pass_success;
        static boolean pass_enter_all_fields;
        static boolean pass_password_not_equal;
        static boolean pass_empty_image;
        static boolean isGalleryDisplaying;

        @Override
        public void onSuccess() {
            if (MockRegisterService.mockDatabase.size() == 1) {
                pass_success = true;
            }
        }

        @Override
        public void onError(String errorMessage) {
            switch (errorMessage) {
                case RegisterPresenter.ENTER_ALL_FIELDS:
                    pass_enter_all_fields = true;
                    break;
                case RegisterPresenter.PASSWORD_NOT_EQUAL:
                    pass_password_not_equal = true;
                    break;
                case RegisterPresenter.EMPTY_IMAGE:
                    pass_empty_image = true;
                    break;
            }
        }

        @Override
        public void displayLoadingCircle() {

        }

        @Override
        public void hideLoadingCircler() {

        }

        @Override
        public void loadImageFromGallery() {
            isGalleryDisplaying = true;
        }
    }

    static class MockRegisterService implements IRegister.IRegisterService {
        static List<CustomerModel> mockDatabase = new ArrayList<>();

        @Override
        public void insertCustomerToDB(CustomerModel model) {
            model = new CustomerModel(
                    model.getUser_username(),
                    model.getUser_password(),
                    model.getUser_fullname(),
                    model.getUser_status(),
                    model.getUser_email(),
                    model.getInputStream());
            mockDatabase.add(model);
        }
    }
}
