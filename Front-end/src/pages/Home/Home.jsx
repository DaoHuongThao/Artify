import React from 'react'
import NavBar from '../../components/NavBar'
import Hero from '../../components/Hero'
import PostCard from '../../components/PostCard'
import FooterPart from '../../components/FooterPart'

export default function Home() {


  return (
    <div className='w-full bg-gray-100'>
      <div className="flex justify-center items-center ">
        <div className="w-full">
          <NavBar />
          <Hero />
          <div className='m-10 flex flex-col items-center justify-center'>
            <PostCard />
          </div>
          <FooterPart/>
        </div>
      </div>
    </div>

  )

}
