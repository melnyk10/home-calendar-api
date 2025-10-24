insert into target (provider_id, source_id, name, type, data)
VALUES
  ((SELECT id AS provider_id FROM provider WHERE type = 'HLTV'), 'hltv:team:9565', 'Vitality', 'TEAM', '{"slug": "vitality", "id": 9565, "url": "https://www.hltv.org/team/9565/vitality"}'),
  ((SELECT id AS provider_id FROM provider WHERE type = 'HLTV'), 'hltv:team:11297', 'The MongolZ', 'TEAM', '{"slug": "the-mongolz", "id": 11297, "url": "https://www.hltv.org/team/11297/the-mongolz"}'),
  ((SELECT id AS provider_id FROM provider WHERE type = 'HLTV'), 'hltv:team:8297', 'FURIA', 'TEAM', '{"slug": "furia", "id": 8297, "url": "https://www.hltv.org/team/8297/furia"}'),
  ((SELECT id AS provider_id FROM provider WHERE type = 'HLTV'), 'hltv:team:4494', 'MOUZ', 'TEAM', '{"slug": "mouz", "id": 4494, "url": "https://www.hltv.org/team/4494/mouz"}'),
  ((SELECT id AS provider_id FROM provider WHERE type = 'HLTV'), 'hltv:team:7020', 'Spirit', 'TEAM', '{"slug": "spirit", "id": 7020, "url": "https://www.hltv.org/team/7020/spirit"}'),
  ((SELECT id AS provider_id FROM provider WHERE type = 'HLTV'), 'hltv:team:12334', 'Falcons', 'TEAM', '{"slug": "falcons", "id": 12334, "url": "https://www.hltv.org/team/12334/falcons"}'),
  ((SELECT id AS provider_id FROM provider WHERE type = 'HLTV'), 'hltv:team:5995', 'G2', 'TEAM', '{"slug": "g2", "id": 5995, "url": "https://www.hltv.org/team/5995/g2"}'),
  ((SELECT id AS provider_id FROM provider WHERE type = 'HLTV'), 'hltv:team:4608', 'Natus Vincere', 'TEAM', '{"slug": "natus-vincere", "id": 4608, "url": "https://www.hltv.org/team/4608/natus-vincere"}'),
  ((SELECT id AS provider_id FROM provider WHERE type = 'HLTV'), 'hltv:team:12110', 'Aurora', 'TEAM', '{"slug": "aurora", "id": 12110, "url": "https://www.hltv.org/team/12110/aurora"}'),
  ((SELECT id AS provider_id FROM provider WHERE type = 'HLTV'), 'hltv:team:5479', '3DMAX', 'TEAM', '{"slug": "3dmax", "id": 5479, "url": "https://www.hltv.org/team/5479/3dmax"}'),
  ((SELECT id AS provider_id FROM provider WHERE type = 'HLTV'), 'hltv:team:6667', 'FaZe', 'TEAM', '{"slug": "faze", "id": 6667, "url": "https://www.hltv.org/team/6667/faze"}'),
  ((SELECT id AS provider_id FROM provider WHERE type = 'HLTV'), 'hltv:team:5973', 'Liquid', 'TEAM', '{"slug": "liquid", "id": 5973, "url": "https://www.hltv.org/team/5973/liquid"}'),
  ((SELECT id AS provider_id FROM provider WHERE type = 'HLTV'), 'hltv:team:6665', 'Astralis', 'TEAM', '{"slug": "astralis", "id": 6665, "url": "https://www.hltv.org/team/6665/astralis"}'),
  ((SELECT id AS provider_id FROM provider WHERE type = 'HLTV'), 'hltv:team:4773', 'paiN', 'TEAM', '{"slug": "pain", "id": 4773, "url": "https://www.hltv.org/team/4773/pain"}'),
  ((SELECT id AS provider_id FROM provider WHERE type = 'HLTV'), 'hltv:team:10915', 'Legacy', 'TEAM', '{"slug": "legacy", "id": 10915, "url": "https://www.hltv.org/team/10915/legacy"}'),
  ((SELECT id AS provider_id FROM provider WHERE type = 'HLTV'), 'hltv:team:9928', 'GamerLegion', 'TEAM', '{"slug": "gamerlegion", "id": 9928, "url": "https://www.hltv.org/team/9928/gamerlegion"}'),
  ((SELECT id AS provider_id FROM provider WHERE type = 'HLTV'), 'hltv:team:12261', 'Gentle Mates', 'TEAM', '{"slug": "gentle-mates", "id": 12261, "url": "https://www.hltv.org/team/12261/gentle-mates"}'),
  ((SELECT id AS provider_id FROM provider WHERE type = 'HLTV'), 'hltv:team:4863', 'TYLOO', 'TEAM', '{"slug": "tyloo", "id": 4863, "url": "https://www.hltv.org/team/4863/tyloo"}'),
  ((SELECT id AS provider_id FROM provider WHERE type = 'HLTV'), 'hltv:team:7175', 'HEROIC', 'TEAM', '{"slug": "heroic", "id": 7175, "url": "https://www.hltv.org/team/7175/heroic"}'),
  ((SELECT id AS provider_id FROM provider WHERE type = 'HLTV'), 'hltv:team:12685', 'Inner Circle', 'TEAM', '{"slug": "inner-circle", "id": 12685, "url": "https://www.hltv.org/team/12685/inner-circle"}');

-- tmp:
insert into "user" (first_name, last_name, email)
values ('Sam', 'Porter-Bridge', 'o.melnyk10@gmail.com');

insert into user_subscription (user_id, target_id)
values ((select id from "user" where email = 'o.melnyk10@gmail.com'),
        (select id from target where name = 'Natus Vincere'));
