package emp.project.softwareengineeringprojectcustomer.Presenter;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.sql.SQLException;
import java.util.List;

import emp.project.softwareengineeringprojectcustomer.Interface.IHome;
import emp.project.softwareengineeringprojectcustomer.Models.Bean.CartModel;
import emp.project.softwareengineeringprojectcustomer.Models.Bean.ProductModel;

public class HomePresenter implements IHome.IHomePresenter {
    private IHome.IHomeView view;
    private IHome.IHomeService service;

    public HomePresenter(IHome.IHomeView view, IHome.IHomeService service) {
        this.view = view;
        this.service = service;
    }

    @Override
    public void loadCategories() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    view.displayProgressBarCategories();
                    List<String> categories = service.getCategories();
                    view.displayRecyclerViewCategory(categories);
                    view.hideProgressBarCategories();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });
        thread.start();
    }

    @Override
    public void onCategoryButtonClicked(String category) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                view.displayProgressBarProducts();
                try {
                    List<ProductModel> productModelList = service.getProducts(category);
                    view.displayRecyclerViewHomeProducts(productModelList);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                view.hideProgressBarProducts();
            }
        });
        thread.start();

    }

    @Override
    public void loadProducts() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                view.displayProgressBarProducts();
                try {
                    List<ProductModel> productModelList = service.getProducts(service.getCategories().get(0));
                    view.displayRecyclerViewHomeProducts(productModelList);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                view.hideProgressBarProducts();
            }
        });
        thread.start();
    }

    public static final String PRODUCT_NOT_ENOUGH = "Product is not enough!";
    public static final String SUCCESS_ADD_TO_CART = "Successfully added to cart!";
    public static final String SUCCESS_UPDATE_TO_CART = "Successfully updated to cart!";

    @Override
    public void onConfirmButtonClicked(String totalNumberOrders, ProductModel model) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                view.displayProgressBarProducts();

                //TODO:FIX ASAP CART PROBLEM
                try {
                    if (Integer.parseInt(totalNumberOrders) < service.checkIfProductIsEnough(model.getProduct_id())) {
                        if (CartModel.getInstance().getTotalNumberOfOrders().equals("0")) {
                            CartModel.getInstance().addToCart(model);
                            view.hideProgressBarProducts();
                            view.displayMessage(SUCCESS_ADD_TO_CART);
                        } else {
                            for (int i = 0; i < Integer.parseInt(CartModel.getInstance().getTotalNumberOfOrders()); i++) {
                                if (model.getProduct_id().equals(CartModel.getInstance().getCartValues().get(i).getProduct_id())) {
                                    CartModel.getInstance().updateToCart(Integer.parseInt(model.getTotal_number_products_orders()), i);
                                    view.displayMessage(SUCCESS_UPDATE_TO_CART);
                                } else {
                                    CartModel.getInstance().addToCart(model);
                                    view.displayMessage(SUCCESS_ADD_TO_CART);
                                }
                                view.hideProgressBarProducts();
                            }
                        }
                    } else {
                        view.displayMessage(PRODUCT_NOT_ENOUGH);
                        view.hideProgressBarProducts();
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
