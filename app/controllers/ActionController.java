package controllers;
import models.util.http.Parameters;
import play.Logger;
import play.api.mvc.Call;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Controller;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Results;

public class ActionController extends Action.Simple {
	public Promise<Result> call(Context ctx) throws Throwable {
		Parameters.clearRequestData();
		
		Call loginPage = routes.Page.login("");
		Call loginAction = routes.AuthController.login();
		
		String request = ctx.request().toString().replace("?contentonly", "");
		
		boolean isLoginPage = request.equals(loginPage.method() + " " + loginPage.url()) || request.equals(loginAction.method() + " " + loginAction.url());
		
		if (AuthController.check() == false && isLoginPage == false) {
	        return Promise.pure(Results.redirect(loginPage));
		}
		else if (AuthController.check() && isLoginPage) {
			return Promise.pure(Results.redirect(routes.Page.index()));
		}
		return delegate.call(ctx);
	}
}