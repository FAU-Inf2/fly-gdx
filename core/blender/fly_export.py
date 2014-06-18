import math
import json
import bpy

from mathutils import Vector, Euler



def convert_position(pos):
	return float('%.4f' % pos)

def convert_angle(angle):
	return float('%.4f' % math.degrees(angle))

def convert_id(id):
	return int(id.replace("Gate.", ""))


def write_level(context, export):
	print("create level data")
	
	data = { }
	
	data['id'] = int(export.level_id)
	data['name'] = export.level_name

	player = bpy.data.objects["Player"]
	
	viewDir = Vector((0.0, 0.0, -1.0))
	viewDir.rotate(player.rotation_euler) 
	
	data['start'] = { }
	data['start']['position'] = { 'x': convert_position(player.location.x), 'y': convert_position(player.location.y), 'z': convert_position(player.location.z) }
	data['start']['viewDirection'] = { 'x': convert_position(viewDir.x), 'y': convert_position(viewDir.y), 'z': convert_position(viewDir.z) }
	
	data['dependencies'] = { }
	data['dependencies']["space"] = "spacesphere"
	data['dependencies']["torus"] = "torus"
	data['dependencies']["hole"] = "torusHoleBox"
	data['dependencies']["asteroid"] = "asteroid"	
	
	data['scripts'] = [ ]

	gates = [ ]

	dummy_gate = { }
	dummy_gate['successors'] = [ ]
	for gateId in bpy.data.cameras["Player"]["start_gates"]:
		dummy_gate['successors'].append(int(gateId))
	gates.append(dummy_gate)
	
	for item in bpy.data.objects:
		if "Gate" in item.name:
			gate = { }
			gate['id'] = convert_id(item.name)
			gate['display'] = item.name
			gate['goal'] = item.name + "hole"

			successors = [ ]
			for constraint in item.constraints:
				successors.append(convert_id(constraint.target.name))

			gate['successors'] = successors
			gates.append(gate)
	
	data['gates'] = gates

	components = [ ]
	
	component = { }
	component['id'] = "space"
	component['ref'] = export.border_model
	components.append(component)

	for item in bpy.data.objects:
		if "Gate" in item.name:
			component = { }
			component['id'] = item.name
			component['ref'] = export.gate_model
			component['position'] = [ convert_position(item.location.x), convert_position(item.location.y), convert_position(item.location.z) ]
			component['euler'] = [ convert_angle(item.rotation_euler.x) - 90.0, convert_angle(item.rotation_euler.y), convert_angle(item.rotation_euler.z) ]
			components.append(component)
		
			component = { }
			component['id'] = item.name + "hole"
			component['ref'] = export.hole_model
			components.append(component)
		
	data['components'] = components

	print("write level")
	
	f = open(export.filepath, 'w', encoding='utf-8')
	f.write(json.dumps(data, sort_keys=True, indent=4, separators=(',', ': ')))
	f.close()

	return {'FINISHED'}


from bpy_extras.io_utils import ExportHelper
from bpy.props import StringProperty, BoolProperty, EnumProperty
from bpy.types import Operator


class ExportLevelOperator(Operator, ExportHelper):
	bl_idname = "level_export.invoke"
	bl_label = "Export Fly Level"

	filename_ext = ".json"
	filter_glob = StringProperty(default="*.json", options={'HIDDEN'})

	level_id = StringProperty(name="Level ID", description="ID of the Level", default="1");
	level_name = StringProperty(name="Level Name", description="Name of the Level", default="Level");
	
	border_model = StringProperty(name="Border Model", description="Border model if no border model property is added", default="space");
	gate_model = StringProperty(name="Gate Model", description="Gate model if no gate model property is added", default="torus");
	hole_model = StringProperty(name="Hole Model", description="Hole model if no hole model property is added", default="hole");

	def execute(self, context):
		return write_level(context, self)


def menu_func_export(self, context):
	self.layout.operator(ExportLevelOperator.bl_idname, text="FLY Exporter (.json)")


def register():
	bpy.utils.register_class(ExportLevelOperator)
	bpy.types.INFO_MT_file_export.append(menu_func_export)


def unregister():
	bpy.utils.unregister_class(ExportLevelOperator)
	bpy.types.INFO_MT_file_export.remove(menu_func_export)


if __name__ == "__main__":
	register()

	bpy.ops.level_export.invoke('INVOKE_DEFAULT')