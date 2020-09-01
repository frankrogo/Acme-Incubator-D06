
package acme.features.administrator.toolRecord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.configurations.Configuration;
import acme.entities.toolRecords.ToolRecord;
import acme.features.administrator.configuration.AdministratorConfigurationRepository;
import acme.framework.components.Model;
import acme.framework.components.Request;
import acme.framework.entities.Administrator;
import acme.framework.services.AbstractShowService;

@Service
public class AdministratorToolRecordShowService implements AbstractShowService<Administrator, ToolRecord> {

	@Autowired
	AdministratorToolRecordRepository				repository;

	@Autowired
	private AdministratorConfigurationRepository	configurationRepository;


	@Override
	public boolean authorise(final Request<ToolRecord> request) {
		assert request != null;
		return true;
	}

	@Override
	public void unbind(final Request<ToolRecord> request, final ToolRecord entity, final Model model) {
		assert request != null;
		assert entity != null;
		assert model != null;
		request.unbind(entity, model, "sector", "title", "inventorName", "description", "website", "email", "stars");
		Configuration config = this.configurationRepository.findOneConfiguration();
		String paramConfig = config.getActivitySectors();
		String[] params = paramConfig.split(",");
		model.setAttribute("params", params);
		String source = entity.getIsOpenSource() ? "open-source" : "closed-source";
		model.setAttribute("source", source);
	}

	@Override
	public ToolRecord findOne(final Request<ToolRecord> request) {
		assert request != null;

		ToolRecord res;
		int id;
		id = request.getModel().getInteger("id");
		res = this.repository.findOneById(id);

		return res;
	}

}
