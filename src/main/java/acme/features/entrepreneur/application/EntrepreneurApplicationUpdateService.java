
package acme.features.entrepreneur.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.applications.Application;
import acme.entities.forums.Forum;
import acme.entities.investmentRounds.InvestmentRound;
import acme.entities.messengers.Messenger;
import acme.entities.roles.Entrepreneur;
import acme.features.authenticated.messenger.AuthenticatedMessengerRepository;
import acme.features.entrepreneur.forum.EntrepreneurForumRepository;
import acme.framework.components.Errors;
import acme.framework.components.Model;
import acme.framework.components.Request;
import acme.framework.entities.Authenticated;
import acme.framework.entities.Principal;
import acme.framework.services.AbstractUpdateService;

@Service
public class EntrepreneurApplicationUpdateService implements AbstractUpdateService<Entrepreneur, Application> {

	@Autowired
	EntrepreneurApplicationRepository	repository;
	@Autowired
	EntrepreneurForumRepository			forumRepository;
	@Autowired
	AuthenticatedMessengerRepository	messengerRepository;


	@Override
	public boolean authorise(final Request<Application> request) {
		assert request != null;

		boolean result;
		int applicationId;
		Application application;
		Entrepreneur entrepreneur;
		Principal principal;

		applicationId = request.getModel().getInteger("id");
		application = this.repository.findOneById(applicationId);
		entrepreneur = application.getInvestmentRound().getEntrepreneur();
		principal = request.getPrincipal();
		result = entrepreneur.getUserAccount().getId() == principal.getAccountId();
		return result;
	}

	@Override
	public void bind(final Request<Application> request, final Application entity, final Errors errors) {
		assert request != null;
		assert entity != null;
		assert errors != null;
		request.bind(entity, errors);
		request.getModel().setAttribute("investmentRoundTicker", entity.getInvestmentRound().getTicker());
		request.getModel().setAttribute("investmentRoundTitle", entity.getInvestmentRound().getTitle());
	}

	@Override
	public void unbind(final Request<Application> request, final Application entity, final Model model) {
		assert request != null;
		assert entity != null;
		assert model != null;
		request.unbind(entity, model, "status", "reasonRejected");
	}

	@Override
	public Application findOne(final Request<Application> request) {
		assert request != null;

		Application result;
		int id;

		id = request.getModel().getInteger("id");
		result = this.repository.findOneById(id);

		return result;
	}

	@Override
	public void validate(final Request<Application> request, final Application entity, final Errors errors) {
		assert request != null;
		assert entity != null;
		assert errors != null;
		errors.state(request, this.checkReasonReject(entity), "reasonRejected", "entrepreneur.application.error.reasonRejected");
		errors.state(request, this.checkOtherDecision(entity), "reasonRejected", "entrepreneur.application.error.otherDecision");

	}
	private boolean checkReasonReject(final Application a) {
		if (a.getStatus().equals("rejected")) {
			return !a.getReasonRejected().equals("");
		}
		return true;
	}

	private boolean checkOtherDecision(final Application a) {
		if (!a.getStatus().equals("rejected")) {
			return a.getReasonRejected().equals("");
		}
		return true;
	}

	@Override
	public void update(final Request<Application> request, final Application entity) {
		assert request != null;
		assert entity != null;
		entity.setStatus(request.getModel().getString("status"));
		if (request.getModel().getString("status").equals("accepted")) {
			InvestmentRound ir = entity.getInvestmentRound();
			Forum forum = this.forumRepository.findOneByInvestmentRoundId(ir.getId());

			Messenger messenger = new Messenger();
			Authenticated authenticated = this.repository.findOneAuthenticatedByUserAccountId(entity.getInvestor().getUserAccount().getId());
			messenger.setAuthenticated(authenticated);
			messenger.setForum(forum);
			messenger.setOwnsTheForum(false);
			this.messengerRepository.save(messenger);

		}
		this.repository.save(entity);
	}

}
