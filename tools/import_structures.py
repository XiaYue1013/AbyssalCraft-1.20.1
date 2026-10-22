"""Converts the upstream AbyssalCraft 1.12.2 structure templates for LOCAL development builds.

The templates are treated as restricted content like the textures (see README.md): they are written to the
git-ignored src/restricted/resources and must never be published.

usage: python tools/import_structures.py [path/to/upstream/AbyssalCraft]   (default: ../_upstream/AbyssalCraft)

1.12.2 assets/abyssalcraft/structures/**.nbt -> data/abyssalcraft/structures/**.nbt
  * AbyssalCraft palette entries are rewritten to the flattened 1.20.1 blocks (the 1.12.2 "stone"/"brick" blocks with
    a type property became one block per type). Vanilla entries are left as they are: the files keep their
    DataVersion (1343), so Minecraft's own data fixer upgrades them when the template is loaded.
  * Statues kept their facing in the block entity in 1.12.2; it becomes the block state's facing.
  * Block entity ids are renamed, 1.12.2 capability data ("ForgeCaps") is dropped, and AbyssalCraft items inside
    containers are renamed (metadata variants became separate items).
"""
import glob
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import nbt  # noqa: E402

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
UPSTREAM = os.path.abspath(sys.argv[1] if len(sys.argv) > 1 else os.path.join(ROOT, '..', '_upstream', 'AbyssalCraft'))
SRC = os.path.join(UPSTREAM, 'src', 'main', 'resources', 'assets', 'abyssalcraft', 'structures')
OUT = os.path.join(ROOT, 'src', 'restricted', 'resources', 'data', 'abyssalcraft', 'structures')

# 1.12.2 block (+ the value of its "type" property, if any) -> 1.20.1 block
BLOCKS = {
    ('stone', 'abyssalnitestone'): 'abyssalnite_stone', ('stone', 'dreadstone'): 'dreadstone', ('stone', 'ethaxium'): 'ethaxium',
    ('stone', 'monolithstone'): 'monolith_stone', ('stone', 'omotholstone'): 'omothol_stone', ('stone', 'darkstone'): 'darkstone',
    ('stone', 'abyssalstone'): 'abyssal_stone', ('stone', 'coraliumstone'): 'coralium_stone',
    ('cobblestone', 'dreadstone'): 'dreadstone_cobblestone', ('cobblestone', 'darkstone'): 'darkstone_cobblestone',
    ('cobblestone', 'abyssalstone'): 'abyssal_cobblestone', ('cobblestone', 'abyssalnite'): 'abyssalnite_cobblestone',
    ('cobblestone', 'coraliumstone'): 'coralium_cobblestone',
    ('abydreadbrick', 'normal'): 'abyssalnite_stone_bricks', ('abydreadbrick', 'chiseled'): 'chiseled_abyssalnite_stone_bricks',
    ('abydreadbrick', 'cracked'): 'cracked_abyssalnite_stone_bricks',
    ('dreadbrick', 'normal'): 'dreadstone_bricks', ('dreadbrick', 'chiseled'): 'chiseled_dreadstone_bricks', ('dreadbrick', 'cracked'): 'cracked_dreadstone_bricks',
    ('ethaxiumbrick', 'normal'): 'ethaxium_bricks', ('ethaxiumbrick', 'chiseled'): 'chiseled_ethaxium_bricks', ('ethaxiumbrick', 'cracked'): 'cracked_ethaxium_bricks',
    ('darkethaxiumbrick', 'normal'): 'dark_ethaxium_bricks', ('darkethaxiumbrick', 'chiseled'): 'chiseled_dark_ethaxium_bricks',
    ('darkethaxiumbrick', 'cracked'): 'cracked_dark_ethaxium_bricks',
    ('ingotblock', 'dreadium'): 'dreadium_block', ('ingotblock', 'abyssalnite'): 'abyssalnite_block',
    ('ingotblock', 'refinedcoralium'): 'refined_coralium_block', ('ingotblock', 'ethaxium'): 'ethaxium_block',
    'abydreadbrickfence': 'abyssalnite_stone_brick_fence', 'dreadbrickfence': 'dreadstone_brick_fence',
    'ethaxiumfence': 'ethaxium_brick_fence', 'darkethaxiumbrickfence': 'dark_ethaxium_brick_fence',
    'abydreadbrickslab1': 'abyssalnite_stone_brick_slab', 'dreadbrickslab1': 'dreadstone_brick_slab', 'ethaxiumbrickslab1': 'ethaxium_brick_slab',
    'darkethaxiumbrickslab1': 'dark_ethaxium_brick_slab', 'dreadstonecobblestoneslab1': 'dreadstone_cobblestone_slab',
    'ethaxiumbrickstairs': 'ethaxium_brick_stairs', 'darkethaxiumbrickstairs': 'dark_ethaxium_brick_stairs',
    'ethaxiumpillar': 'ethaxium_pillar', 'darkethaxiumpillar': 'dark_ethaxium_pillar', 'monolithpillar': 'monolith_pillar',
    'cwater': 'liquid_coralium', 'antiwater': 'liquid_antimatter',
    'crate': 'wooden_crate', 'crystallizer': 'crystallizer', 'engraver': 'engraver', 'shoggothblock': 'shoggoth_ooze',
    # mob blocks, registered together with their mobs (until then the template loads them as air)
    'dreadguardspawner': 'dreadguard_spawner', 'chagarothspawner': 'chagaroth_spawner', 'jzaharspawner': 'jzahar_spawner',
    'gatekeeperminionspawner': 'minion_of_the_gatekeeper_spawner', 'shoggothbiomass': 'shoggoth_biomass',
}
STATUES = {'cthulhu', 'hastur', 'jzahar', 'azathoth', 'nyarlathotep', 'yogsothoth', 'shubniggurath'}
STATUE_NAMES = {'yogsothoth': 'yog_sothoth', 'shubniggurath': 'shub_niggurath'}
FACINGS = {2: 'north', 3: 'south', 4: 'west', 5: 'east'}

# block entity ids; None drops the data (the block creates a fresh block entity)
BLOCK_ENTITIES = {
    'abyssalcraft:tileentitycrate': 'abyssalcraft:crate', 'abyssalcraft:tileentitystatue': 'abyssalcraft:statue',
    'minecraft:tileentitystatue': 'abyssalcraft:statue',
    'abyssalcraft:tileentitycrystallizer': None, 'abyssalcraft:tileentityengraver': None,
    'abyssalcraft:tileentitydecorativestatue': None, 'minecraft:tileentitydecorativestatue': None,
    'abyssalcraft:tileentitydradguardspawner': None, 'abyssalcraft:tileentitychagarothspawner': None,
    'minecraft:tileentitygatekeeperminionspawner': None, 'minecraft:tileentityjzaharspawner': None,
    'abyssalcraft:tileentityshoggothbiomass': None,
}

# (1.12.2 item, damage) -> 1.20.1 item; damage None matches any
ITEMS = {
    ('anticorflesh', None): 'anti_plagued_flesh', ('anticorbone', None): 'anti_plagued_flesh_on_a_bone',
    ('antidote', 0): 'coralium_antidote', ('antidote', 1): 'dread_antidote',
    ('beefp', None): 'beef_on_a_plate', ('chickenp', None): 'chicken_on_a_plate', ('porkp', None): 'pork_on_a_plate',
    ('fishp', None): 'fish_on_a_plate', ('eggp', None): 'fried_egg_on_a_plate', ('friedegg', None): 'fried_egg', ('mre', None): 'mre',
    ('corflesh', None): 'coralium_plagued_flesh', ('corbone', None): 'coralium_plagued_flesh_on_a_bone',
    ('dreadfragment', None): 'dread_fragment', ('dreadsapling', None): 'dreadlands_sapling', ('wastelandsthorn', None): 'wastelands_thorn',
    ('shoggothflesh', 0): 'shoggoth_flesh_overworld', ('shoggothflesh', 1): 'shoggoth_flesh_abyssal_wasteland',
    ('shoggothflesh', 2): 'shoggoth_flesh_dreadlands', ('shoggothflesh', 3): 'shoggoth_flesh_omothol', ('shoggothflesh', 4): 'shoggoth_flesh_dark_realm',
}

unmapped = set()


def convert_state(state, facing=None):
    """Returns the converted palette entry (a new compound)"""
    name = state['Name'][1]
    props = dict(state.get('Properties', (nbt.COMPOUND, {}))[1])
    if not name.startswith('abyssalcraft:'):
        return state
    old = name.split(':', 1)[1]
    kind = props.get('type', (nbt.STRING, None))[1]
    if old in ('statue', 'decorativestatue') and kind in STATUES:
        deity = STATUE_NAMES.get(kind, kind)
        new = (deity + '_statue') if old == 'statue' else ('decorative_' + deity + '_statue')
        props = {'facing': (nbt.STRING, facing or props.get('facing', (nbt.STRING, 'north'))[1])}
    elif (old, kind) in BLOCKS:
        new = BLOCKS[(old, kind)]
        props.pop('type', None)
    elif old in BLOCKS:
        new = BLOCKS[old]
    else:
        unmapped.add((old, kind))
        return state
    if old.endswith('slab1'):  # 1.12.2 half=top/bottom (+ unused variant) -> type=top/bottom
        props = {'type': (nbt.STRING, props.get('half', (nbt.STRING, 'bottom'))[1])}
    out = {'Name': (nbt.STRING, 'abyssalcraft:' + new)}
    if props:
        out['Properties'] = (nbt.COMPOUND, props)
    return out


def convert_item(item):
    name = item['id'][1]
    if not name.startswith('abyssalcraft:'):
        return item
    old = name.split(':', 1)[1]
    damage = item.get('Damage', (nbt.SHORT, 0))[1]
    new = ITEMS.get((old, damage)) or ITEMS.get((old, None))
    if new is None:
        unmapped.add(('item', old, damage))
        return item
    out = dict(item)
    out['id'] = (nbt.STRING, 'abyssalcraft:' + new)
    out.pop('Damage', None)
    return out


def convert_block_entity(data):
    data = dict(data)
    data.pop('ForgeCaps', None)
    old = data.get('id', (nbt.STRING, ''))[1]
    if old in BLOCK_ENTITIES:
        if BLOCK_ENTITIES[old] is None:
            return None
        data['id'] = (nbt.STRING, BLOCK_ENTITIES[old])
    elif old.startswith('abyssalcraft:') or old.startswith('minecraft:tileentity'):
        unmapped.add(('block entity', old))
    if 'Items' in data:
        element, items = data['Items'][1]
        data['Items'] = (nbt.LIST, (element, [convert_item(i) for i in items]))
    return data


def convert(path, out_path):
    name, root = nbt.load(path)
    palette = root['palette'][1][1]
    new_palette, index = [], {}

    def state_index(state):
        key = repr(sorted((k, repr(v)) for k, v in state.items()))
        if key not in index:
            index[key] = len(new_palette)
            new_palette.append(state)
        return index[key]

    converted = [convert_state(s) for s in palette]
    blocks = []
    for block in root['blocks'][1][1]:
        block = dict(block)
        state = palette[block['state'][1]]
        entity = block.get('nbt', (nbt.COMPOUND, None))[1]
        facing = FACINGS.get(entity.get('Facing', (nbt.INT, None))[1]) if entity else None
        new_state = convert_state(state, facing) if facing and state['Name'][1] in ('abyssalcraft:statue', 'abyssalcraft:decorativestatue') \
            else converted[block['state'][1]]
        block['state'] = (nbt.INT, state_index(new_state))
        if entity is not None:
            entity = convert_block_entity(entity)
            if entity is None:
                del block['nbt']
            else:
                block['nbt'] = (nbt.COMPOUND, entity)
        blocks.append(block)
    root['palette'] = (nbt.LIST, (nbt.COMPOUND, new_palette))
    root['blocks'] = (nbt.LIST, (nbt.COMPOUND, blocks))
    os.makedirs(os.path.dirname(out_path), exist_ok=True)
    nbt.save(out_path, name, root)


def main():
    files = sorted(glob.glob(os.path.join(SRC, '**', '*.nbt'), recursive=True))
    for path in files:
        rel = os.path.relpath(path, SRC)
        convert(path, os.path.join(OUT, rel))
    print('structures converted: %d -> %s' % (len(files), OUT))
    if unmapped:
        print('unmapped:', sorted(unmapped, key=str))


if __name__ == '__main__':
    main()
