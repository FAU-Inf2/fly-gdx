package de.fau.cs.mad.fly.graphics.shaders;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;

/**
 * Created by tschaei on 21.08.14.
 */
public class FlyNormalShader extends FlyBaseShader {
    
    private String VERTEX_SHADER = "shaders/vertex.glsl";
    private String FRAGMENT_SHADER = "shaders/normalmap.fragment.glsl";
    private int u_diffuseColor, normalMap;
    
    @Override
    public void init() {
        createShaderProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        super.init();
        
        u_diffuseColor = program.getUniformLocation("u_diffuseColor");
        normalMap = program.getUniformLocation("normalMap");
    }
    
    @Override
    public int compareTo(Shader other) {
        return 0;
    }
    
    @Override
    public void render(Renderable renderable) {
        super.setUpBaseUniforms(renderable);
        
        program.setUniformf(u_diffuseColor, ((ColorAttribute) renderable.material.get(ColorAttribute.Diffuse)).color);
        // Bind texture
        ((TextureAttribute) renderable.material.get(TextureAttribute.Normal)).textureDescription.texture.bind(1);
        program.setUniformi(normalMap, 1);
        
        renderable.mesh.render(program, renderable.primitiveType, renderable.meshPartOffset, renderable.meshPartSize);
    }
    
    @Override
    public boolean canRender(Renderable renderable) {
        return (renderable.environment.equals(environment) && !renderable.material.has(TextureAttribute.Diffuse) && renderable.material.has(TextureAttribute.Normal));
    }
}
