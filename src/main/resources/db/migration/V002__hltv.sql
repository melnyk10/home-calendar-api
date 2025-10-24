WITH hltv_provider AS (
    SELECT id AS provider_id FROM provider WHERE type = 'HLTV'
)
INSERT INTO event_target (provider_id, name, type, data)
SELECT
    hltv_provider.provider_id,
    team.name,
    'TEAM',
    team.data::jsonb
FROM hltv_provider,
     (
         VALUES
             ('Vitality', 'TEAM', '{"slug": "vitality", "id": 9565, "url": "https://www.hltv.org/team/9565/vitality"}'),
             ('The MongolZ', 'TEAM', '{"slug": "the-mongolz", "id": 11297, "url": "https://www.hltv.org/team/11297/the-mongolz"}'),
             ('FURIA', 'TEAM', '{"slug": "furia", "id": 8297, "url": "https://www.hltv.org/team/8297/furia"}'),
             ('MOUZ', 'TEAM', '{"slug": "mouz", "id": 4494, "url": "https://www.hltv.org/team/4494/mouz"}'),
             ('Spirit', 'TEAM', '{"slug": "spirit", "id": 7020, "url": "https://www.hltv.org/team/7020/spirit"}'),
             ('Falcons', 'TEAM', '{"slug": "falcons", "id": 12334, "url": "https://www.hltv.org/team/12334/falcons"}'),
             ('G2', 'TEAM', '{"slug": "g2", "id": 5995, "url": "https://www.hltv.org/team/5995/g2"}'),
             ('Natus Vincere', 'TEAM', '{"slug": "natus-vincere", "id": 4608, "url": "https://www.hltv.org/team/4608/natus-vincere"}'),
             ('Aurora', 'TEAM', '{"slug": "aurora", "id": 12110, "url": "https://www.hltv.org/team/12110/aurora"}'),
             ('3DMAX', 'TEAM', '{"slug": "3dmax", "id": 5479, "url": "https://www.hltv.org/team/5479/3dmax"}'),
             ('FaZe', 'TEAM', '{"slug": "faze", "id": 6667, "url": "https://www.hltv.org/team/6667/faze"}'),
             ('Liquid', 'TEAM', '{"slug": "liquid", "id": 5973, "url": "https://www.hltv.org/team/5973/liquid"}'),
             ('Astralis', 'TEAM', '{"slug": "astralis", "id": 6665, "url": "https://www.hltv.org/team/6665/astralis"}'),
             ('paiN', 'TEAM', '{"slug": "pain", "id": 4773, "url": "https://www.hltv.org/team/4773/pain"}'),
             ('Legacy', 'TEAM', '{"slug": "legacy", "id": 10915, "url": "https://www.hltv.org/team/10915/legacy"}'),
             ('GamerLegion', 'TEAM', '{"slug": "gamerlegion", "id": 9928, "url": "https://www.hltv.org/team/9928/gamerlegion"}'),
             ('Gentle Mates', 'TEAM', '{"slug": "gentle-mates", "id": 12261, "url": "https://www.hltv.org/team/12261/gentle-mates"}'),
             ('TYLOO', 'TEAM', '{"slug": "tyloo", "id": 4863, "url": "https://www.hltv.org/team/4863/tyloo"}'),
             ('HEROIC', 'TEAM', '{"slug": "heroic", "id": 7175, "url": "https://www.hltv.org/team/7175/heroic"}'),
             ('Inner Circle', 'TEAM', '{"slug": "inner-circle", "id": 12685, "url": "https://www.hltv.org/team/12685/inner-circle"}')
) AS team(name, data);

-- tmp:
insert into "user" (first_name, last_name, email)
values ('Sam', 'Porter-Bridge', 'o.melnyk10@gmail.com');

insert into user_subscription (user_id, target_id)
values ((select id from "user" where email = 'o.melnyk10@gmail.com'),
        (select id from event_target where name = 'Natus Vincere'));
