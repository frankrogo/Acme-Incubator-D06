
package acme.features.administrator.technologyRecord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.configurations.Configuration;
import acme.entities.technologyRecords.TechnologyRecord;
import acme.features.administrator.configuration.AdministratorConfigurationRepository;
import acme.framework.components.Model;
import acme.framework.components.Request;
import acme.framework.entities.Administrator;
import acme.framework.services.AbstractShowService;

@Service
public class AdministratorTechnologyRecordShowService implements AbstractShowService<Administrator, TechnologyRecord> {

	@Autowired
	AdministratorTechnologyRecordRepository			repository;

	@Autowired
	private AdministratorConfigurationRepository	configurationRepository;


	@Override
	public boolean authorise(final Request<TechnologyRecord> request) {
		assert request != null;
		return true;
	}

	@Override
	public void unbind(final Request<TechnologyRecord> request, final TechnologyRecord entity, final Model model) {
		assert request != null;
		assert entity != null;
		assert model != null;
		request.unbind(entity, model, "stars", "title", "sector", "inventorName", "description", "website", "email");
		Configuration config = this.configurationRepository.findOneConfiguration();
		String paramConfig = config.getActivitySectors();
		String[] params = paramConfig.split(",");
		model.setAttribute("params", params);
		String source = entity.getIsOpenSource() ? "open-source" : "closed-source";
		model.setAttribute("source", source);
	}

	@Override
	public TechnologyRecord findOne(final Request<TechnologyRecord> request) {
		assert request != null;

		TechnologyRecord res;
		int id;
		id = request.getModel().getInteger("id");
		res = this.repository.findOneById(id);

		return res;
	}

}
