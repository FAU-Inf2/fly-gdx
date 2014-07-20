import math
import json
import bpy

from mathutils import Vector, Euler


class ConvertHelper:
	"""Helper class for converting blender data to export data"""
	
	@staticmethod
	def convert_pos(pos):
		"""Converts the position to floating point and cuts too many decimal places"""
		return float('%.4f' % pos)

	@staticmethod
	def convert_angle(angle):
		"""Converts the angle to degree and cuts too many decimal places"""
		return float('%.4f' % math.degrees(angle))

	@staticmethod
	def convert_id(id):
		"""Converts the object id to an integer number"""
		return int(id.replace("Gate.", ""))



class LevelExporter:
	"""Creates the dictionary of the blender data and exports it to a json file"""
	
	def __init__(self, export):
		self.data = { }
		
	def setup(self, export):
		"""Creates the dictionary of the blender data"""
		print("create level data")
	
		self.setupInfo(export)
		
		self.data['start'] = self.setupStartPos()
		self.data['dependencies'] = self.setupDependencies()
		self.data['gates'] = self.setupGates()
		self.data['components'] = self.setupComponents(export)
		
	def setupInfo(self, export):
		"""Creates the level information"""
		self.data['id'] = int(export.level_id)
		self.data['name'] = export.level_name
		self.data['time'] = int(export.level_time)
		self.data['scripts'] = [ ]
		
	def setupStartPos(self):
		"""Creates the starting position information"""
		player = bpy.data.objects["Player"]
    
		viewDir = Vector((0.0, 0.0, -1.0))
		viewDir.rotate(player.rotation_euler)
		
		upDir = Vector((0.0, 1.0, 0.0))
		upDir.rotate(player.rotation_euler)
		
		start = { }
		start['position'] = { 'x': ConvertHelper.convert_pos(player.location.x), 'y': ConvertHelper.convert_pos(player.location.y), 'z': ConvertHelper.convert_pos(player.location.z) }
		start['viewDirection'] = { 'x': ConvertHelper.convert_pos(viewDir.x), 'y': ConvertHelper.convert_pos(viewDir.y), 'z': ConvertHelper.convert_pos(viewDir.z) }
		start['upDirection'] = { 'x': ConvertHelper.convert_pos(upDir.x), 'y': ConvertHelper.convert_pos(upDir.y), 'z': ConvertHelper.convert_pos(upDir.z) }
		return start
		
	def setupDependencies(self):
		"""Creates the dependency information"""
		deps = { }
		for model in bpy.data.objects["Player"].keys():
			if model not in '_RNA_UI':
				deps[model] = bpy.data.objects["Player"][model]

		return deps
		
	def setupGates(self):
		"""Creates the gate information"""
		gates = [ ]

		dummy_gate = { }
		dummy_gate['successors'] = [ ]
		for constraint in bpy.data.objects["Player"].constraints:
			dummy_gate['successors'].append(ConvertHelper.convert_id(constraint.target.name))
		gates.append(dummy_gate)
		
		for item in bpy.data.objects:
			if "Gate" in item.name:
				gate = { }
				gate['id'] = ConvertHelper.convert_id(item.name)
				gate['display'] = item.name
				gate['goal'] = item.name + "hole"

				successors = [ ]
				for constraint in item.constraints:
					successors.append(ConvertHelper.convert_id(constraint.target.name))

				gate['successors'] = successors
				gates.append(gate)
		
		return gates
		
	def setupComponents(self, export):
		"""Creates the component information"""
		components = [ ]
		
		component = { }
		component['id'] = "space"
		component['ref'] = export.border_model
		components.append(component)

		for item in bpy.data.objects:
			if "Player" not in item.name:
				component = { }
				component['id'] = item.name
				component['ref'] = item['Model']
				component['position'] = [ ConvertHelper.convert_pos(item.location.x), ConvertHelper.convert_pos(item.location.y), ConvertHelper.convert_pos(item.location.z) ]
				component['euler'] = [ ConvertHelper.convert_angle(item.rotation_euler.x) - 90.0, ConvertHelper.convert_angle(item.rotation_euler.y), ConvertHelper.convert_angle(item.rotation_euler.z) ]
				if item.scale.x != 1.0 or item.scale.y != 1.0 or item.scale.z != 1.0:
					component['scale'] = [ ConvertHelper.convert_pos(item.scale.x), ConvertHelper.convert_pos(item.scale.y), ConvertHelper.convert_pos(item.scale.z) ]
				if "Visible" in item:
					if "false" in item['Visible']:
						component['visible'] = False
				components.append(component)
				
				if "Gate" in item.name:
					component = { }
					component['id'] = item.name + "hole"
					component['ref'] = item['HoleModel']
					component['visible'] = False
					components.append(component)
			
		return components

	def writeLevel(self, filepath):
		"""Exports the file to the specified filepath"""
		print("write level")
	
		f = open(filepath, 'w', encoding='utf-8')
		f.write(json.dumps(self.data, sort_keys=True, indent=4, separators=(',', ': ')))
		f.close()





def write_level(context, export):
	levelExporter = LevelExporter(export)
	
	levelExporter.setup(export)
	
	levelExporter.writeLevel(export.filepath)

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
	level_time = StringProperty(name="Level Time", description="Time of the Level", default="30");
	
	border_model = StringProperty(name="Border Model", description="Border model if no border model property is added", default="space");

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