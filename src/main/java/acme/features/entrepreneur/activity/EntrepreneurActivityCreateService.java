
package acme.features.entrepreneur.activity;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.activities.Activity;
import acme.entities.investmentRounds.InvestmentRound;
import acme.entities.roles.Entrepreneur;
import acme.framework.components.Errors;
import acme.framework.components.Model;
import acme.framework.components.Request;
import acme.framework.datatypes.Money;
import acme.framework.entities.Principal;
import acme.framework.services.AbstractCreateService;

@Service
public class EntrepreneurActivityCreateService implements AbstractCreateService<Entrepreneur, Activity> {

	@Autowired
	EntrepreneurActivityRepository repository;


	@Override
	public boolean authorise(final Request<Activity> request) {
		boolean result;
		InvestmentRound investmentRound;
		Entrepreneur entrepreneur;
		Principal principal;

		investmentRound = this.repository.findInvestmentRoundById(request.getModel().getInteger("investmentRoundId"));
		entrepreneur = investmentRound.getEntrepreneur();
		principal = request.getPrincipal();
		result = entrepreneur.getUserAccount().getId() == principal.getAccountId();
		return result;
	}

	@Override
	public void bind(final Request<Activity> request, final Activity entity, final Errors errors) {
		assert request != null;
		assert entity != null;
		assert errors != null;
		request.bind(entity, errors, "investmentRoundId");
	}

	@Override
	public void unbind(final Request<Activity> request, final Activity entity, final Model model) {
		assert request != null;
		assert entity != null;
		assert model != null;
		request.unbind(entity, model, "title", "deadline", "budget");
		model.setAttribute("investmentRoundId", request.getModel().getInteger("investmentRoundId"));
		model.setAttribute("budget", "0€");

	}

	@Override
	public Activity instantiate(final Request<Activity> request) {
		assert request != null;
		Activity result = new Activity();
		result.setCreationMoment(new Date(System.currentTimeMillis() - 1));
		InvestmentRound investmentRound = this.repository.findInvestmentRoundById(request.getModel().getInteger("investmentRoundId"));
		result.setInvestmentRound(investmentRound);
		return result;
	}

	@Override
	public void validate(final Request<Activity> request, final Activity entity, final Errors errors) {
		assert request != null;
		assert entity != null;
		assert errors != null;
		Collection<Activity> activities = this.repository.findManyByInvestmentRoundId(entity.getInvestmentRound().getId());
		InvestmentRound ivr = entity.getInvestmentRound();
		if (!errors.hasErrors("budget")) {
			Double actualBudget = entity.getBudget().getAmount();
			errors.state(request, actualBudget != null, "budget", "entrepreneur.activity.error.budget.null");
			Double resta = 0.0;
			boolean res = this.overAmount(activities, ivr, actualBudget); //si false, la activity actual excede el amount
			if (res == false && !errors.hasErrors("budget")) {//en ese caso
				resta = this.quantityLeft(activities, ivr, actualBudget);//print cuanto me he pasado
				errors.state(request, res, "budget", "entrepreneur.activity.error.budget.overAmount" + resta + "EUR");
			}
		}

	}

	private boolean overAmount(final Collection<Activity> activities, final InvestmentRound ivr, final Double actualBudget) {
		boolean res = true;
		Double ivrAmount = ivr.getMoneyAmount().getAmount();
		Double rest = 0.0;
		for (Activity a : activities) {
			Double acMoney = a.getBudget().getAmount();
			rest = acMoney + rest ;
		}
		if (rest + actualBudget > ivrAmount) {
			res = false;
		} else {
			return res;
		}
		return res;
	}

	private Double quantityLeft(final Collection<Activity> activities, final InvestmentRound ivr, final Double actualBudget) {
		Double rest = 0.0;
		Double resta = 0.0;
		Double acMoney =0.0;
		Double ivrAmount = ivr.getMoneyAmount().getAmount();
		for (Activity a : activities) {
			acMoney = acMoney + a.getBudget().getAmount();
			
		}
		rest = actualBudget + acMoney;
		
		resta = rest - ivrAmount;
		return resta;
	}

	@Override
	public void create(final Request<Activity> request, final Activity entity) {
		assert request != null;
		assert entity != null;
		this.repository.save(entity);

	}

}
