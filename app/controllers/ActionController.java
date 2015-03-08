package controllers;
import models.util.http.Parameters;
import play.api.mvc.Call;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Results;

public class ActionController extends Action.Simple {
	public Promise<Result> call(Context ctx) throws Throwable {
		Parameters.clearRequestData();
		
		Call loginPage = routes.Page.login("");
		
		boolean isLoginPage = ctx.request().toString().equals(loginPage.method() + " " + loginPage.url());
		
		if (AuthController.check() == false && isLoginPage == false) {
	        return Promise.pure(Results.redirect(loginPage));
		}
		else if (AuthController.check() && isLoginPage) {
			return Promise.pure(Results.redirect(routes.Page.index()));
		}
		return delegate.call(ctx);
	}
}