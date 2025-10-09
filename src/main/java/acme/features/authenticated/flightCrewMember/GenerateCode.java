
package acme.features.authenticated.flightCrewMember;

import java.util.Random;

import acme.client.components.basis.AbstractRole;
import acme.client.components.principals.DefaultUserIdentity;

public class GenerateCode {

	public static String generate(final AbstractRole role) {
		if (role == null || role.getIdentity() == null)
			throw new IllegalArgumentException("acme.validation.flightCrewMember.role.null");

		DefaultUserIdentity identity = role.getIdentity();

		String name = identity.getName();
		String surname = identity.getSurname();

		if (name == null || surname == null || name.isEmpty() || surname.isEmpty())
			throw new IllegalArgumentException("acme.validation.flightCrewMember.name.null");

		String initials = name.substring(0, 1).toUpperCase() + surname.substring(0, 1).toUpperCase();

		String codePrefix = initials;
		if (surname.length() > 1)
			if (codePrefix.length() == 2)
				initials += surname.substring(1, 2).toUpperCase();

		StringBuilder numbers = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < 6; i++)
			numbers.append(random.nextInt(10));

		return initials + numbers;
	}

}
