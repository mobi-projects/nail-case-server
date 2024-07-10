package com.nailcase.testUtils.fixture;

import static org.jeasy.random.FieldPredicates.*;

import java.util.Set;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;

import com.nailcase.model.entity.NailArtist;
import com.nailcase.model.enums.Role;
import com.nailcase.testUtils.StringGenerateFixture;

public class NailArtistFixture {

	private static final Long MEMBER_ID = 1L;

	private static final Role ROLE = Role.MEMBER;

	public NailArtist getNailArtist() {
		EasyRandomParameters params = new EasyRandomParameters()
			.randomize(named("nailArtistId"), () -> MEMBER_ID)
			.randomize(named("email"), () -> StringGenerateFixture.makeEmail(10))
			.randomize(named("role"), () -> ROLE)
			.excludeField(named("shops").and(ofType(Set.class)));
		return new EasyRandom(params).nextObject(NailArtist.class);
	}

}
